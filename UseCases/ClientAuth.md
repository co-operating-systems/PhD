# Client Authorization

The Solid community often raises use cases of limiting client access. But there are two very different types of use cases encompassed by this idea: the user wishing to limit access to various resources, and the server wishing to limit access by certain apps.

## User Limitations on clients

Most users will wish to limit newly downloaded apps to certain safe spaces to test them out and learn to gain confidence in them. They may thereafter be happy to enlarge the space of resources those apps are allowed access locally or extend the websites they are allowed to access. These restrictions need only be visible to the Launcher App, which can use those to decide when to sign headers for the app.

Can we use the same WAC ontology to express those limitations?

### 1. Limiting access to a local folder

The Launcher App could write the following rule to a
place that only it can read:

```turtle
@prefix : <http://www.w3.org/ns/auth/acl#> .

<#r1> a :Authorization;
  :mode :Read, :Write;
  :agent <https://photo.app/demoV#>;
  :accessToClass [ :subdirs </app/photo/> ] .
```

The above rule would tell the Launcher App that the photo app could only read and write to any subdirectory of the `</app/photo/>` folder. The app would be able to read and write to `</app/photo/2021/04/01/>` but not to `</app/banking/2024/>` for example.

### 2. Limiting access to specific types of websites 

Another rule could be to only allow a banking app access to banking websites.

```turtle
<#r2> a :Authorization.
   :mode :Read, Write;
   :agent <https://banking.app/view#>;
   :accessToClass won:BankingWebSites.
```

The question then becomes: how does one define `won:BankingWebSites` ? Let us assume it is defined by a future [Web Of Nations](https://co-operating.systems/2020/06/01/WoN/) standard ([pdf](https://co-operating.systems/2020/06/01/WoN.pdf)). That may come with a proof procedure
that requires the Wallet to find out if an accessed website can be reached via the users national trust chain. 

So imagine that Dorothy who lives in Kansas, has her FreedomBox at home running a Solid POD. This information is known to her Launcher App. So let us say the banking app wants to fetch some resource on [credit-agricole.fr](https://www.credit-agricole.fr/). How would the LauncherApp's Wallet know if credit-agricole is a `won:BankingWebSite` or not? The procedure would be here to start from the [kansas.gov](https://kansas.gov) website and find the link pointing to [usa.gov](https://usa.gov/) which would contain links to the countries in diplomatic relations to the USA, and a link also to Kansas proving that kansas is part of the USA. The credit-agricole website would in the same way link to a French company registrar [infogreffes](https://www.infogreffe.fr) with a RDF translatable representation for [Credit-Agricole de la Brie](https://www.infogreffe.fr/entreprise/caisse-locale-credit-agricole-de-la-brie/413588948/d2ebb654-e060-471b-8772-e20de6cafd86), and that representation should link to the French root [gouv.fr](https://gouv.fr/) which would point back to Infogreffes and to all the similar documents in all the other countries that are diplomatically related to France, of which of course the USA is one. From this one can then build a chain of trust from the Kansas POD to the Credit-Agricole website which is perhaps the only direction that is needed.
Now if the description in the French registrar contains a relation 

```turtle
@prefix crAgr: <https://www.infogreffe.fr/entreprise/caisse-locale-credit-agricole-de-la-brie/413588948/d2ebb654-e060-471b-8772-e20de6cafd86#> .

crAgr:co a won:BankingWebSite;
   foaf:homepage <https://www.credit-agricole.fr/> . 
```

Then that plus the chain of links from Kansas to Infogreffe constitutes a proof that the Credit-Agricole website is a `won:BankingWebSite` and hence that the banking app is allowed to access it, and so that the Wallet in the Launcher App can sign requests going to the credit-agricole web site.

todo: it would be interesting to express this chain of trust as a set of N3 rules.  

### Relationship to BAN

In "§6 Roles and Programs" of the 1992 paper [Authentication in Distributed Systems: Theory and Practice](https://dl.acm.org/doi/pdf/10.1145/138873.138874) the authors write "A principal often wants to limit its authority, in order to express the fact that it is acting according to a certain set of rules." That is, in a way what we are doing when the Wallet limits what resources a client can access. Can we use some of the concepts from the paper?

BAN defines the composite Principle `A as R` where A is the main Principal and R is a role, as something that can be understood with


`(A as R) says s <=> A says (R says s)`

That is `A as R` is the principal `A | R`, understood as A speaking in role R.  It is clear that the limitation on an App is creating a new Principal for that App and that it is a self-imposed limitation.

This makes it clear that the client authorization rules we set out
above are not quite right, as we have identified the software as the agent. Ie, we wrote:

```turtle
<#r2> a :Authorization.
   :mode :Read, Write;
   :agent <https://banking.app/view#>;
   :accessToClass won:BankingWebSites.
```

We don't want to limit the banking app in general, but our use of the banking App, in this case, Alice's.

Or if several people could use that app in Alice's family,
perhaps Alice wants to limit her use of it, in which case she would
need a rule that is closer to the BAN idea of Alice as BankingApp 

```turtle
<#r2> a :Authorization.
   :mode :Read, Write;
   :agent [ :id </alice#>;
            :as <https://alice.name/apps/banking/a#> ];
   :accessToClass won:BankingWebSites.
```

todo: this needs to be thought through more carefully.



## Server limitations on clients

Many use cases for limiting access of clients to servers can be implemented using client-side restrictions as shown in the previous section. Where possible it is much preferable that restrictions on apps be placed on the client side and not on the data production side, as it leaves much more freedom for apps to evolve, and reduces the work on the server to keep track of good and bad apps.

Nevertheless, we can imagine that some data providers may want access to be limited to certified apps. 

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

So this Web Access control rule expresses that the class of agents that can have access to the `</client/>` container on the bank's web site are only customers of the bank that are using CertifiedApps. 

CertifiedApps could be defined as a union of a number of lists produced by different app certifiers, and `credAgr:Customer` could be potentially defined by some regex on the Bank WebID of the customer, so perhaps something like

```Turtle
@prefix pwdr: <http://www.w3.org/2007/05/powder-s#> .

credAgr:Customer a owl:Class;
   powder:domain <https://credit-agricole.fr>;
   powder:pathregex "/accnt/*/id#" . #prop is made up
```

[todo: find out the best way to express this]


### Proof of App being used

How would the Guard on the server know that the given app was being used by the customer. There are two ways this could be done:

1. Weak: the Wallet could add a header specifying the client WebID used in the headers and then sign that with the customer key. This would allow the user to override the ID of an app to try out other ones, but that would be his responsibility and his risk to take.
2. Strong: the app could provide a header signed by a private key linked to a well-known public key of the app. A simple way to do that would be for the App to send a request to some service the app controls that would sign the header. This would be slow and immediately leak all requests to the app owner, so one should look for better solutions. The App could use opaque keys created in the browser that would then be signed by the app to do this. In any case, multiple signatures for the same request are possible with HTTPSig.

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


We may want to delegate access to a resource to another agent. A person may want to delegate 
1. I can delegate to my mobile phone or solid wallet the job of signing requests for me
2. my personal online data store (POD) may crawl the web, send notifications for me while I am doing other things
3. Delegation example from [§2.3.8 Delegation](https://solid.github.io/authorization-panel/authorization-ucr/#uc-delegation-subset) of the Solid Use Cases Document.

## V1: Full delegation

The 2012 paper [Extending the WebID Protocol with Access Delegation
](https://ceur-ws.org/Vol-905/TrampEtAl_COLD2012.pdf) proposed a simple way to do delegation for the WebID-TLS protocol. HttpSig does not suffer from some of the technical limitations mentioned there. For example with TLS, changing the client certificate requires opening a completely new TLS connection. Whereas with HttpSig, each request can be signed with a different key if needed.

### Full access: adding public keys to the WebID

Adding some other agent's key to one's WebID would be equivalent to giving them full power to act everywhere on one's behalf.  This makes sense in at least the following scenarios:

If one thinks of software such as a Wallet as a separate agent and one could well have several wallets, then it makes sense to attach the keys those wallets use directly to one's WebID identity. One could thus still track which wallet was signing requests, but the Guard giving access to remote resources would not need to distinguish any of them.

Another use case is parents adding their key to their children's WebID. 
This would allow them to have access to the websites their children are accessing.

A company server that wanted to act on behalf of its users - e.g. to send out notifications of changes, prefetch content, ... -  would be able to add its key to each of the employee's profiles. This case would not suffer from having to open a new TLS connection for each user, as described in the 2012 paper.

### Declarative statement of full delegation

The 2021 paper also proposes that one could add a `:secretary` relation to one's WebId to another agent to fully act on one's behalf. 

```turtle
@prefix trust: <https://bblfish.net/work/2012/09/trust#>
<#me> trust:secretary </s/riley#>
```

This would allow `</s/riley#>` to sign requests on behalf of `</me#>`. This would be equivalent to adding the key of `</s/riley#>` to `</me#>`.

### The `ban:speaksFor` relation

This is equivalent to what BAN logic (as explained in the 1992 paper [Authentication in Distributed Systems: Theory and Practice](https://dl.acm.org/doi/pdf/10.1145/138873.138874)) is the `speaksFor` relation. 
Indeed we can map one to the other using an n3 rule

```turtle
{ ?a ban:speaksFor ?b } <=> { ?b trust:secretary ?a }
```

or more simply using owl

```Turtle
@prefix owl: <http://www.w3.org/2002/07/owl#> .

ban:speaksFor owl:inverseOf trust:secretary .
```



### Partial access

We can 



