# Social Networking Use Cases

This Chapter described social networking access control use cases in increasing level of complexity. These were the original use cases that motivated the development of Solid.

- [Social Networking Use Cases](#social-networking-use-cases)
  - [One-step indirection: friend network](#one-step-indirection-friend-network)
    - [Client Auth logic](#client-auth-logic)
    - [Server Auth logic](#server-auth-logic)
  - [Three-step indirection: Foaf of a friend](#three-step-indirection-foaf-of-a-friend)
    - [Client Auth logic](#client-auth-logic-1)
    - [Server Auth logic](#server-auth-logic-1)


## One-step indirection: friend network


Now let us consider a slightly more complicated rule that gives read access to the `party` resource on Bob's POD, to Alice's friends, or more precisely, people that have a `foaf:knows` relation to Alice.
For this, we add "Caroline Smith" as having a `foaf:knows` relation from Alice's.

![foaf party](Diagrams/WebID_Foaf_Party.svg)

The rule is specified using a class specified using an owl restriction, that contains all people that are foaf known by Alice.

```Turtle
<#aliceFriendsRule> a :Authorization;
  :mode :Read;
  :agentClass <#aliceFriendsCls>;
  :accessTo <https://bob.name/2023/04/party> .

<#aliceFriendClass> owl:sameAs [  a owl:Restriction;
      owl:onProperty [ owl:inverseOf foaf:knows ];
      owl:hasValue <https://alice.name/card#me>
   ].
```


### Client Auth logic

The client needs to find if any of its identities satisfies the class defined by the restriction. An owl reasoner with access to the user's trusted data should be able to solve that problem: it just needs to ask what objects satisfy that class in the graph which is the union of the user's data. 

Given the set of identifiers that satisfy that rule, it will need to find which of those ids have a key to sign the request. Of those it will need to find out which of those keys can be used as proof to convince the Guard on the server.

### Server Auth logic

The Server must receive proof that allows it to go from the rule to the key that signed the request in logical steps.

The server could crawl Alice's friend network and just recognize the key if used. But that puts a lot of the burden of proof on the Guard. Alice may have 10 thousand connections or more. One of those connections may have been added recently and so Bob's Guard may need to check Alice's network every time a signed request comes in where it cannot recognize the key.

Instead, it would be much easier for the client to provide a proof. 
I first proposed this with the `WAC-Hint` header in the [Auth.md](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/http/auth/Auth.md) document of Reactive-SoLiD.

What we need is to tell the Guard to follow this process:

1. start from the `<#aliceFriendsRule>` in the ACL file
2. verify this rule applies for the request of course - that should be implicit
3. follow the `:agentClass` link to `<#aliceFriendsClass>`
4. the class is defined as anything related by `foaf:knows` to `<https://alice.name/card#me>`, so we can start from Alice's WebID.
5. from Alice's WebID we can follow the `foaf:knows` relation to Caroline's WebID - So we have one element of that class
6. from Caroline's WebID we can follow the `cert:key` relation to the key that signed the request. QED

The above needs to be expressed efficiently in the header of the request in a way the Guard can follow.

## Three-step indirection: Foaf of a friend

We often want to extend access to a resource as widely as possible, while reducing it enough to avoid spam.  This use case was written up in [§2.3.7 Default permissions for extended networks](https://solid.github.io/authorization-panel/authorization-ucr/#inheritance-extended) of the Solid Use Cases Document. It goes as follows:

> Alice has a blog and allows comments on her posts. Ideally, everyone’s comments would be immediately visible, but she has previously been overwhelmed by spammers. So now she would like to try a compromise: allow the posts from her extended social network (friend of her friends, colleagues and family) to be immediately visible. Other posts should only be visible and editable to those who wrote them. They can then be viewable to the world when they get reviewed.

We can illustrate this with the following diagram, where we show a rule expressing that Bob's friends and their friends can write to the comments folder on Alice's blog. The blog post could well be word readable, but the blob owner may wish to have review comments from people that are not known. This rule could of course be a lot more complicated, but this is a good start.

![Extended Network Example](Diagrams/ExtendedNetwork.svg)

We can then define our rule `<#r1>` that gives access to 

```turtle
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix : <http://www.w3.org/ns/auth/acl#> .
@prefix foaf: <http://xmlns.com/foaf/0.1/> .

<#r1> a :Authorization;
  :mode :Write;
  :agentClass </utils/sn#bobFoaf3Cls>;
  :accessTo <https://alice.name/blog/comments/> .
```

And because the `sn:bobFoaf3Cls` class is so useful to
Bob he placed it in his own social network ontology, 
at `<https://bob.name/utils/sn#>` which reads:

```turtle
foaf:knows rdfs:subPropertyOf <#isKnownByMax3> .
:knows2 owl:propertyChainAxiom (foaf:knows foaf:knows);
    rdfs:subPropertyOf <#isKnownByMax3> .
:knows3 owl:propertyChainAxiom (foaf:knows foaf:knows foaf:knows);
    rdfs:subPropertyOf <#isKnownByMax3> .

<#bobFoaf3Cls> owl:sameAs [  a owl:Restriction;
      owl:onProperty :isKnownByMax3;
      owl:hasValue <https://bob.name/card#me>
   ].
```

We here have 
1. a rule with a local definition that is not in the same file
2. a class that groups individuals up to three levels of indirection which is not something the web server Guard should be trying to keep real-time track of as that could easily be a class containing `300*300*300 = 9 million` individuals. 
3. We can easily see that these rules could use arbitrary owl concepts, which means that the client needs to be able to reason with arbitrary owl concepts, produce such proofs, and the server needs to be able to verify them.

So we have a pretty hefty problem to solve.

### Client Auth logic

Imagine that Dan Brickley from danbri.org wants to comment on Bob's blog. His client needs to work out that he belongs to the `sn:BobFoaf3Cls` class. 

His client would need to fetch Bob's foaf graph. (Note that may be split across several resources, some protected some not). We may assume that Dan's client already has a database of his FOAF network. Note: he may not know everyone who knows him, so this could be incomplete. Given these two networks, the client needs to find if there is an intersection between Bob's friend Group and Dan's foaf group. If there is such a group then Dan's client can use this to build a proof of `foaf:knows` relations starting from Bob's WebID to Dan.

Todo: we need to integrate that we don't just want links from Bob's WebId via a `foaf:knows` chain to Dan's WebID, but we also want to consider `foaf:knows` relations that appear in associated `rdfs:seeAlso` documents.

Another way this could be calculated is for Dan's client to consider all the data it has in one large graph, and search all the `foaf:knows` chains between Bob and Dan in there. Given that it would then filter those for which it can construct a proof that would satisfy Bob's Guard. 

The proof that Dan's client should send would contain this chain of links with info about potential jumps from one named graph to the definition graph. This would make it easy for the Guard to verify the logic of the proof. But that proof would need to be tied to the definition in the access control file. So it would need to show that they are related by `:knows3` starting from `https://bob.name/card#me`, and that this means they are related by `:isKnownByMax3` and hence that Dan is in the set `sn:bobFoaf3Cls`.

### Server Auth logic

What type of chain would satisfy Bob's Guard? It has to be a chain that starts from Bob's WebID (and perhaps linked to `rdfs:seeAlso` documents? What other types of links would be legal to look at?) and ends at Dan's WebID. 



