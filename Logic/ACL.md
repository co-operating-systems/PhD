From Access Control Lists to AC Logic.

- [Access Control Lists](#access-control-lists)
  - [Problems with AC Lists](#problems-with-ac-lists)
- [Adding Rules gives Access Control Logic](#adding-rules-gives-access-control-logic)
    - [A typesafe version](#a-typesafe-version)
    - [Using OWL DL](#using-owl-dl)
  - [Using N3](#using-n3)
  - [Providing a proof](#providing-a-proof)


# Access Control Lists 

The simplest Authorization Policy is Access Control Lists. It separates the process of Authentication and Authorization. The Guard on the server (a.k.a a Reference Monitor) can check for any resource and find out if a particular principal can access that resource in the given mode.
This can be implemented as an exhaustive list of atomic ground statements, which would then be easy to index and search, but as we will see not to maintain.

This is nicely explained in Martin Abadi's 2009 [Logic in Access Control Tutorial Notes](https://users.soe.ucsc.edu/~abadi/Papers/fosad-acllogic.pdf) ([Springer](https://link.springer.com/chapter/10.1007/978-3-642-03829-7_5)). All one needs is a three-place predicate $\text{may-access}(p, r, m)$ to define atomic statements so that one can define that Alice may read `foo.txt` as: 

$$
\text{may-access}(\text{Alice}, \text{Foo.txt}, \text{Rd})
$$

Or one could even have three binary predicates, `read`, `write`, `create`, which would work well RDF.

```Turtle
:alice acl:read <Foo> .
:alice acl:write <Foo> .
```

## Problems with AC Lists

Deepak Garg's 2009 PhD dissertation [Proof Theory for Authorization Logic and Its Application to a Practical File System](https://people.mpi-sws.org/~dg/papers/papers.html#theses) lists the following reasons against a purely fact-based representation such as
the above:
1. they do not carry information about why access is allowed
2. it is, therefore, difficult to update the data

Alice may be allowed read access to `</secret/level2/>` because she is a manager with level 2 clearance. The facts could be expressed as

```turtle
:alice acl:read </secret/level2/> .
```

But that does not contain the reason why it is true.

If later her clearance level is reduced to level 1, the simple statement of fact in the database does not indicate that it needs to be changed. Furthermore, no log trace can be kept as to why access was granted.

**Furthermore** in a decentralised system, one cannot know everyone wanting to access a resource. 
* One may want people from other companies with a specific relation to one's company to access. 
* Or perhaps the resource can be accessed by anyone that has paid for it. Of course, once someone has paid, one could update the access control rules by giving that person access. But then one would be back to the previous problems of not knowing why those people have access.
* Finally, one may want to allow access to people that can present a token useable for 3 accesses. That type of access could not be expressed in that simple ACL.

# Adding Rules gives Access Control Logic

How far can one go with just this if we then add rules?

One can give access by role, such as the rule allowing only a Manager above level 2 to see the `<secret>` document.

$$
\forall x. \text{ GovTeam}(x) \land \text{level}(x) \geqslant 2 \rightarrow \text{CanRead}(x, \texttt{secret}) 
$$

The advantage of this rule is that it would not require deleting all the individual facts when someone's role changed in the company.

### A typesafe version

Rather than give access to individual documents that way one could give access to all documents with a certain label.
In a more typesafe notation [^1], we could write the rule that uses a function $\text{level}$ that takes a manager or resource to a level represented as a natural number. (For this we require types to be organized in a partial lattice, as Scala is)

$$
\begin{align}
level &: \text{Person} + \text{Resource} \to \Bbb{N} \\
\forall p&: \text{Person}, r: \text{Resource}.\space \text{GovTeam}(p) \land \text{level}(p) \geqslant \text{level}(r) \rightarrow \text{CanRead}(p, r)
\end{align}
$$
Then given the following facts
$$
\begin{align}
&\text{alice}: \text{Person} \\
&\text{GovTeam}(\text{alice}) = true \\
&\text{level}(\text{alice}) = 2 \\
&\text{level}(\texttt{"/secret/doc"}) = 2 \\
\end{align}
$$

We can build a proof that 
$$\text{CanRead}(\text{alice}, \texttt{"/secret/doc"})$$

### Using OWL DL

What we were doing above with typed logic could also be
done with Description Logic, standardized as [OWL](https://www.w3.org/TR/owl2-primer/)](https://www.w3.org/TR/owl2-primer/). 
We can think of a rule as
linking a set of resources and a set of agents via description.

```turtle
@prefix acl: <http://www.w3.org/ns/auth/acl#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .

<#clearanceLevel2Resources> owl:sameAs [ a owl:Restriction;
        owl:onProperty tag:clearance;
        owl:hasValue "level2" ] .

<#clearanceLevel2Person> owl:sameAs [ a owl:Restriction;
        owl:onProperty security:clearance;
        owl:hasValue 2 ] . 

<#allowCleared> a acl:Authorization;
    acl:modes acl:Read;
    acl:accessToClass  <#clearanceLevel2> ;
    acl:agentClass [ owl:intesectionOf (
        <#clearanceLevel2Person>
        gov:Team
        ) ] .
```

 Finding out if an action meets that rule is to find that
1. the mode matches that of the request
2. the resource is a member of the `accessToClass` and 
3. the agent requesting access fits the description

So the above [Web Access Control](https://solidproject.org/TR/wac) rule extended with OWL descriptions above, states that if the clearance of the agent is 2 and (s)he is a member of the GovTeam, and the required clearance tag on a resource is "level2", then the agent can read the resource.

With OWL we cannot (easily?) link the level in the `agentClass` and the resource class as we did in the Logical rule above, where we specified that the agent's clearance must be greater than or equal to the resource's clearance.

On the other hand, OWL can give us an established standardized vocabulary and complexity guarantees.
todo: How much are complexity guarantees of OWL needed if we want the client, in the end, to pass a proof to the server?


## Using N3

N3 gives us first-order logic, so we could write the rule as

```turtle
{ ?agent security:clearance ?agentClearance .
  ?resource security:clearance ?resourceClearance .
  ?agentClearance >= ?resourceClearance .
} => {
    ?agent acl:read ?resource .
}
```

## Providing a proof

In the example used above the server could just calculate the proof by itself. But as the data space gets larger, so the size of the space to search for a proof grows exponentially, as shown by the  [Social Web use cases](../UseCases/Foaf.md). The client on the other hand can search a much more limited data space, since it knows what keys and credentials it has available to use. Thus the client should provide a proof that the server could check.

For the rules we saw above, it won't be sufficient for the client to just prove its control of a key or WebID. It will also need to prove that the identity is correctly related to the required attributes. In the case of a clearance level that could be done by pointing to an internet resource it knows the Guard trusts or by providing a certificate from a trusted authority. But in any case
it won't be enough for the client to just state that it has those properties. It will have to provide a statement that someone trustworthy claims those properties.

Hence we enter the modal logic of "saying that", discussed in [ABLP](ABLP.md).

Todo: detail some of these proofs.


[^1] [On Type Distinctions and Expressivity](https://twitter.com/DavidCorfield8/status/1628815276702670850) but also [the Tweet by David Corfield](https://twitter.com/DavidCorfield8/status/1628815276702670850) and an interesting point about [presuppositions and consequences](https://twitter.com/DavidCorfield8/status/1689631794604523523)










