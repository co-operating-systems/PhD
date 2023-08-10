
# Client Authorization

The Solid community often raises use cases of limiting client access. But there are two very different types of use cases encompassed by this idea: the user wishing to limit access to various resources, and the server wishing to limit access by certain apps.

- [Client Authorization](#client-authorization)
  - [User Limitations on Clients](#user-limitations-on-clients)
    - [Links to ABLP logic](#links-to-ablp-logic)
    - [1. Limiting access to a local folder](#1-limiting-access-to-a-local-folder)
    - [2. Limiting access to specific types of websites](#2-limiting-access-to-specific-types-of-websites)
  - [Server limitations on clients](#server-limitations-on-clients)
    - [ABLP principal conjunction](#ablp-principal-conjunction)
    - [As a Web Access Control rule](#as-a-web-access-control-rule)
    - [With Proof of App being used](#with-proof-of-app-being-used)


## User Limitations on Clients

Most users will wish to limit newly downloaded apps to safe spaces to test them out. As they gain confidence in an app, users may later want to enlarge the space of resources those apps are allowed access to locally or even extend their reach to the whole web. 

These restrictions can be written out in the form of policies, which need only be known to the wallet - a separate trusted app that can sign for identified apps. On each request by an app to sign a given request the Wallet can consult its policies and decide whether to sign or not. 

The role here is very similar to that of the Guard on the server. It needs a rule to decide given a resource and a mode of access requested by a given agent, should it or not sign the request?

So we ask: Can we use the same WAC ontology to express those limitations?

### Links to ABLP logic

A self-imposed limitation is close to what [ABLP](../Logic/ABLP.md) logic calls a role. The paper "A Calculus for Access Control" says on p11:

> There are many situations in which a principal may wish to reduce his powers. We now describe a few, as motivation for our treatment of roles. They are all examples of the principle of “least privilege,” according to which a principal should have only the privileges it needs to accomplish its task.

A user in a role, written $A \text{ as } R$, is modeled as $A | R$ where $A$ is the user and $R$ is the role. 

$$
A \text{ as } R \equiv A | R 
$$

where $A|R$ is defined as

$$
A|R \text{ says } s \equiv A \text{ says } R \text{ says } s
$$

The client-side limitation is about limiting what someone can do
when in a given role, where the role is the role of using a given app here. Ie. the Wallet is limiting what an agent can say via a given app.

The advantage of having this self-imposed limitation on the client side is that it simplifies the access control logic on the server, which needs only know what Agent is allowed access. 
In a decentralized environment there are uncountably many types of apps that could be produced, and having resources across the web to keep up to date on what apps the user has authorized is not a scalable solution, as that can change at any time as the user's trust in the app increases.

### 1. Limiting access to a local folder

We imagine Alice trying out a new photo App. After adding it to her [Launcher App](https://github.com/bblfish/LauncherApp/) she could set some initial limitations on that app, written out as the following rule. 

```turtle
@prefix : <http://www.w3.org/ns/auth/acl#> .

<#r1> a :Authorization;
  :mode :Read, :Write;
  :agent <https://photo.app/demoV#>;
  :accessToClass [ :subdirs </app/photo/> ] .
```

This should limit the photo app demo to read and write
to only resources in the `</app/photo/>` collection of her POD.

Here `<https://photo.app/demoV#>` is the name of the App type. 
But in RDF we need to think about graphs remaining true even when merged with other true RDF graphs.
But, if we merged such RDF graphs for the preferences of any number of other users for the same photo app, we would end up with that app having access to everything on the web. 

What we need is to specify that it is the user $U$ as app $A$
who should be limited. Ie we need the Authorization to be limited to 
$U \text{ as } A$. Following the suggestion in ABLP [§ roles](../Logic/ABLP.md#roles) we could write our client-side rule as

```turtle
@prefix c: <https://www.w3.org/2001/tag/dj9/speech#>
<#r1> a :Authorization;
  :mode :Read, :Write;
  :agent [ c:principal <#Alice>;
           c:as <https://photo.app/demo#> ]; 
  :accessToClass [ :subdirs </app/photo/> ] .
```

This makes it clear how we can use ABLP roles to limit client-side access to resources for various apps.

Note, instead of using `:accessToClass` we could have used `wac:default`, which has the advantage of being defined and implemented. On the other hand the `:accessToClass` link has the advantage of showing us how very simple concepts from the [RelBac description logic](../Logic/RelBac.md) could be useful.

It looks like we could map between them with an N3 rule such as

```N3
{ ?rule :default ?r} => { ?r :accessToClass [ :subdirs ?r ]}
```


### 2. Limiting access to specific types of websites 

Another rule could be to only allow a banking app access to banking websites.

```turtle
<#r2> a :Authorization.
   :mode :Read, Write;
   :agent [ c:principal <#Alice> ;
            c:as <https://banking.app/view#> ];
   :accessToClass won:BankingWebSites.
```

The bigger problem here is not how to model rules but how to globally define `won:BankingWebSites` ? Let us assume it is defined by a future [Web Of Nations](https://co-operating.systems/2020/06/01/WoN/) standard ([pdf](https://co-operating.systems/2020/06/01/WoN.pdf)). That may come with a proof procedure
that requires the Wallet to find out if an accessed website can be reached via the user's national trust chain. 

So imagine that Dorothy who lives in Kansas, has her FreedomBox at home running a Solid POD. This information is known to her Launcher App. So let us say the banking app wants to fetch some resource on [credit-agricole.fr](https://www.credit-agricole.fr/). How would the LauncherApp's Wallet know if credit-agricole is a `won:BankingWebSite` or not? The procedure would be here to start from the [kansas.gov](https://kansas.gov) website and find the link pointing to [usa.gov](https://usa.gov/) which would contain links to the countries in diplomatic relations to the USA, and a link also to Kansas proving that kansas is part of the USA. The credit-agricole website would in the same way link to a French company registrar [infogreffes](https://www.infogreffe.fr) with an RDF translatable representation for [Credit-Agricole de la Brie](https://www.infogreffe.fr/entreprise/caisse-locale-credit-agricole-de-la-brie/413588948/d2ebb654-e060-471b-8772-e20de6cafd86), and that representation should link to the French root [gouv.fr](https://gouv.fr/) which would point back to Infogreffes and to all the similar documents in all the other countries that are diplomatically related to France, of which of course the USA is one. From this one can then build a chain of trust from the Kansas POD to the Credit-Agricole website which is perhaps the only direction that is needed.
Now if the description in the French registrar contains a relation 

```turtle
@prefix crAgr: <https://www.infogreffe.fr/entreprise/caisse-locale-credit-agricole-de-la-brie/413588948/d2ebb654-e060-471b-8772-e20de6cafd86#> .

crAgr:co a won:BankingWebSite;
   foaf:homepage <https://www.credit-agricole.fr/> . 
```

Then that plus the chain of links from Kansas to Infogreffe constitutes proof that the Credit-Agricole website is a `won:BankingWebSite` and hence that the banking app is allowed to access it, and so that the Wallet in the Launcher App can sign requests going to the Credit-Agricole web site.

todo: it would be interesting to express this chain of trust as a set of N3 rules.  



## Server limitations on clients

Many use cases for limiting access of clients to servers can be implemented using client-side restrictions as shown in the previous section. Where possible it is much preferable that restrictions on apps be placed on the client side and not on the data production side, as it leaves much more freedom for apps to evolve, and reduces the work on the server to keep track of good and bad apps, or of user preferences. 

### ABLP principal conjunction

ABLP allows us to define conjunctions of Principals.
Earlier we saw the $|$ quoting operator on Principals. Similarly, we have the $\land$ operator that takes two
Principals and returns a new Principal that is the conjunction of the two. We are thus to think of principals as forming a partial lattice. The rule is

$$
A \land B \text{ says } s \equiv A \text{ says } s \land B \text{ says } s
$$

With this we can see what the server wants: it wants to know that the right type of agent and the right type of app are together participating in the request.


### As a Web Access Control rule

Using the idea of conjunction of Principals we can 
address the use case where data providers want access to be limited to certified apps. 

They could write this use case out in WAC as:

```Turtle
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix : <http://www.w3.org/ns/auth/acl#> .

<#r3> a :Authorization;
   :mode :Read;
   :default </client/>;
   :agentClass [ 
      owl:intersectionOf ( 
         creAgr:Customer 
         [ a owl:Restriction; 
           owl:onProperty app:isUsing;
           owl:hasValuesFrom bank:CertifiedApp 
         ]) 
   ].
```

So this Web Access control rule expresses that the class of agents that can have access to the `</client/>` container on the bank's website are only customers of the bank that are using CertifiedApps. 

CertifiedApps could be defined as a union of several lists produced by different app certifiers, and `credAgr:Customer` could be potentially defined by some regex on the Bank WebID of the customer, so perhaps something like

```Turtle
@prefix pwdr: <http://www.w3.org/2007/05/powder-s#> .

credAgr:Customer a owl:Class;
   powder:domain <https://credit-agricole.fr>;
   powder:pathregex "/accnt/*/id#" . #prop is made up
```

[todo: find out the best way to express regular expressions now - other methods would also work.]


### With Proof of App being used

How would the Guard on the server know that the given app was being used by the customer? There are two ways this could be done:

1. Weak: the Wallet could add a header specifying the client WebID used in the headers and then sign that with the customer key. This would allow the user to override the ID of an app to try out other ones, but that would be his responsibility and his risk to take.
2. Strong: the app could provide a header signed by a private key linked to a well-known public key of the app. 
   1. A simple way to do that would be for the App to send a request to some service the app controls that would sign the header. This would be slow and immediately leak all requests to the app owner, so one should look for better solutions. 
   2. The App could use opaque keys created in the browser that would then be signed by the app creator's keys. Multiple signatures for the same request are possible with HTTPSig.

Given these two ways, the server would need a way to tell the client which method it will accept. It should be possible to do that by using two relations:

```Turtle
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

app:App a owl:Class;
  rdfs:comment "The class of all Apps. WebIDs of these Apps are contained in documents that link to the authors that wrote them, company that financed the production and maintains code, links to insuraces, and of course to the code or exacutable itself. It can also link to public keys the app can use indirectly to sign headers to prove it is running the code". 

app:isUsing a owl:ObjectProperty;
   rdfs:comment "relation linking an agent to the app they are using. ";
   rdfs:domain foaf:Agent;
   rdfs:range app:App .

app:isProvablyUsing a owl:ObjectProperty;
   rdfs:comment "relation linking an agent to the app they are using and can prove they are using it. The proof would consist in showing that something was signed with a key linked to the app.";
   rdfs:domain foaf:Agent;
   rdfs:range app:App .
```

With definitions such as those it would be possible for the server to request
a signature from an App with proof that the app is Certified by publishing
the following AC Resource:

```Turtle
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix : <http://www.w3.org/ns/auth/acl#> .

<#r3> a :Authorization;
   :mode :Read;
   :default </client/>;
   :agentClass [ 
      owl:intersectionOf ( 
         creAgr:Customer 
         [ a owl:Restriction; 
           owl:onProperty app:isProvablyUsing;
           owl:hasValuesFrom bank:CertifiedApp 
         ]) 
   ].
```

The HTTPBis' draft RFC [Message Signatures](https://datatracker.ietf.org/doc/draft-ietf-httpbis-message-signatures/) allows for multiple signatures from different agents, so that would be one way to implement Principal conjunction.

