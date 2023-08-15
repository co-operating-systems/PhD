The W3C verifiable Credentials standards ([VC Data Model](https://www.w3.org/TR/vc-data-model/), ...) come with [use cases](https://www.w3.org/TR/vc-use-cases/) that we can use to test our logic, and later our implementation.
For Verifiable Credentials to really work at a Global scale, we will need to implement a [Web of Nations](./WoN.md), but one can get part of the way there even without those yet.

So below I will look at various use cases as they come to my attention.

# Credentials data Model

The [VC Data Model](https://www.w3.org/TR/vc-data-model/) is the core of the Verifiable Credentials standards and is itself an example of something that should be explained in terms of the [says logic](../Logic/ABLP.md).

Todo: do that here.

# Use Cases

## Only Trust certain Issuers of Identity


This is use case [§2.8.1 from Use Case and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/#uc-trustedissuers). The answer proposed here was first suggested in
[issue 176 of the Authorization Panel](https://github.com/solid/authorization-panel/issues/176). It leads to the [Functional Requirement §3.1.2](https://solid.github.io/authorization-panel/authorization-ucr/#req-trusted-identity)
> The system shall allow access to be limited based on the identity of the agent, only when that identity is issued by a trusted identity provider.

The VC Data Model 1.1 defines a [issuer field](https://www.w3.org/TR/vc-data-model/#issuer) which the [json profile](https://www.w3.org/2018/credentials/v1) tells us refers to the [cred:issuer](https://www.w3.org/2018/credentials/#) relation [defined in turtle](https://www.w3.org/2018/credentials/vocabulary.ttl) as

```Turtle
@prefix cred: <https://w3.org/2018/credentials#> .

cred:issuer a rdfs:Property, owl:ObjectProperty ;
    rdfs:domain cred:VerifiableCredential ;
    rdfs:label "Issuer" ;
    rdfs:comment """<div>The value of this property must be a URL uniquely identifying the issuer.</div>"""^^rdf:HTML ;
    rdfs:isDefinedBy <https://www.w3.org/TR/vc-data-model-2.0/#defn-issuer>, <https://w3.org/2018/credentials#> ;
    vs:term_status "stable" .
```

If one takes the various definitions listed above one gets to see that the range of the relation is essentially a URI that identifies an agent, so a WebID or did, or similar URIs.

But we also need a way to go from the agent to the credential. 
The VC Data Model 1.1 also defines a [credentialSubject](https://www.w3.org/TR/vc-data-model/#credential-subject) field which the [json profile](https://www.w3.org/2018/credentials/v1) tells us refers to the [cred:credentialSubject](https://www.w3.org/2018/credentials/#credentialSubject) relation [defined in turtle](https://www.w3.org/2018/credentials/vocabulary.ttl) as

```Turtle
cred:credentialSubject a rdfs:Property, owl:ObjectProperty ;
    rdfs:domain cred:VerifiableCredential ;
    rdfs:label "Credential subject" ;
    rdfs:comment """<div>An entity about which claims are made.</div>"""^^rdf:HTML ;
    rdfs:isDefinedBy <https://www.w3.org/TR/vc-data-model-2.0/#defn-credentialSubject>, <https://w3.org/2018/credentials#> ;
    vs:term_status "stable" ;
.
```

With these two relations in hand we can define a class of agents whose credential is issued by a trusted issuer.

First, we define a relation that goes from agent to its credential followed by a relation from the credential to the issuer.

```Turtle
<#hasCredentialIssuer> owl:propertyChainAxiom (
    [ owl:inverseOf cred:credentialSubject ]
    cred:issuer 
)
```

Then we if we have a set of trusted issuers named `trusted:issuers` we can define a class of agents whose credential is issued by a trusted issuer as

And finally, with that, we can write a WAC rule which gives access to agents that could present a credential issued by one of the trusted issuers.


```turtle
@prefix acl: <http://www.w3.org/ns/auth/acl#>  .

<#trCredRule> a acl:Authorization;
    acl:agentClass  :credentialedAgents;  
    acl:mode    acl:Read, acl:Write;  
    acl:default <comments/>
```




