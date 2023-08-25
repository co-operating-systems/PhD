![Person showing a passport to a border guard questioning the resemblance to the picture](https://www.vmcdn.ca/f/files/victoriatimescolonist/json/2022/06/web1_raeside_passport_web.jpg;w=960;h=640;bgcolor=000000)

We discuss the Verifiable Credentials model and logic in [the logic section](../Logic/VerifiableCredentials.md).

The W3C verifiable Credentials standards ([VC Data Model](https://www.w3.org/TR/vc-data-model/), ...) comes with [use cases](https://www.w3.org/TR/vc-use-cases/) that we can use to test our logic, and later our implementation. Some of the [Use Case and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/) require Credentials, so we should also look at them.
For Verifiable Credentials to really work at a Global scale, we will need to implement a [Web of Nations](./WoN.md), but one can get part of the way there even without those yet.

So below I will look at various use cases as they come to my attention.

- [Only Trust certain Issuers of Identity](#only-trust-certain-issuers-of-identity)
  - [Building the WAC rule with the VC Ontology](#building-the-wac-rule-with-the-vc-ontology)
    - [The subject of the Credential](#the-subject-of-the-credential)
    - [The issuer of the Credential](#the-issuer-of-the-credential)
    - [Building the hasCredentialIssuer relation](#building-the-hascredentialissuer-relation)
    - [Defining the credentialed agents class](#defining-the-credentialed-agents-class)
    - [Build the WAC rule](#build-the-wac-rule)
  - [Example Signed University Credential](#example-signed-university-credential)
  - [Client Proof procedures](#client-proof-procedures)
  - [Server Guard Proof procedure](#server-guard-proof-procedure)



# Only Trust certain Issuers of Identity

This is use case [§2.8.1 from Use Case and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/#uc-trustedissuers). The answer proposed here was first suggested in
[issue 176 of the Authorization Panel](https://github.com/solid/authorization-panel/issues/176). It leads to the [Functional Requirement §3.1.2](https://solid.github.io/authorization-panel/authorization-ucr/#req-trusted-identity)

> The system shall allow access to be limited based on the identity of the agent, only when that identity is issued by a trusted identity provider.

Note that this use-case is not telling us what kind of claim we should be interested in, only that we want to know that the claim is made by one of a set of agents. We assume that the Agent is known to make only certain types of claims and not sign a claim such as "we do not know this person". 


The use case could be understood in one of two ways:
1. The identity provider is an agency such as Twitter, Github, Google, ... which provides OAuth access to an identity
2. The identity is provided via a Verifiable Credential, signed by one or more issuers

According to the `says` logic, both should be treated the same way. In both cases, an authority is making a claim about an agent. In one case, it is a locally stored signed document where the signature allows us to tell who made the claim, and in the other case, we listen to the authority make the claim directly - though this does involve TLS signatures going on in the background. The proof procedures to verify the claims are different, but the logic once the claims are verified is the same.


## Building the WAC rule with the VC Ontology

We need a relation from an agent with a credential to the credential issuer so that we can then define a class of agents with that relation to trusted issuers.
We can find both of those relations in the VC ontology.


### The subject of the Credential

First, we need a relation to go from the agent to the credential. 
The VC Data Model 1.1 defines a [credentialSubject](https://www.w3.org/TR/vc-data-model/#credential-subject) field which the [JSON profile](https://www.w3.org/2018/credentials/v1) tells us refers to the [cred:credentialSubject](https://www.w3.org/2018/credentials/#credentialSubject) relation [defined in Turtle](https://www.w3.org/2018/credentials/vocabulary.ttl) as

```Turtle
cred:credentialSubject a rdfs:Property, owl:ObjectProperty ;
    rdfs:domain cred:VerifiableCredential ;
    rdfs:label "Credential subject" ;
    rdfs:comment """<div>An entity about which claims are made.</div>"""^^rdf:HTML ;
    rdfs:isDefinedBy <https://www.w3.org/TR/vc-data-model-2.0/#defn-credentialSubject>, <https://w3.org/2018/credentials#> ;
    vs:term_status "stable" ;
.
```

### The issuer of the Credential

The VC Data Model 1.1 defines an [issuer field](https://www.w3.org/TR/vc-data-model/#issuer) which the [json profile](https://www.w3.org/2018/credentials/v1) tells us refers to the [cred:issuer](https://www.w3.org/2018/credentials/#) relation [defined in Turtle](https://www.w3.org/2018/credentials/vocabulary.ttl) as

```Turtle
@prefix cred: <https://w3.org/2018/credentials#> .

cred:issuer a rdfs:Property, owl:ObjectProperty ;
    rdfs:domain cred:VerifiableCredential ;
    rdfs:label "Issuer" ;
    rdfs:comment """<div>The value of this property must be a URL uniquely identifying the issuer.</div>"""^^rdf:HTML ;
    rdfs:isDefinedBy <https://www.w3.org/TR/vc-data-model-2.0/#defn-issuer>, <https://w3.org/2018/credentials#> ;
    vs:term_status "stable" .
```

If one puzzles together the various definitions listed above one gets to see that the range of the relation is essentially a URI that identifies an agent, so a WebID or did, or similar URIs.

### Building the hasCredentialIssuer relation

Using these two relations we define a relation that goes from an agent to its credential followed by a relation from the credential to the issuer. 
We have to reverse the first relation credentialSubject relation to do this.

```Turtle
<#hasCredentialIssuer> owl:propertyChainAxiom (
    [ owl:inverseOf cred:credentialSubject ]
    cred:issuer 
)
```

### Defining the credentialed agents class

We can now define a class of agents whose credential is issued by a trusted issuer.

For this, we need a class of `:TrustedIssuers` which we can build in any number of ways.
That allows us to define a class of agents whose credential is issued by a trusted issuer as

```Turtle
<#credentialedAgents> owl:sameAs [ a owl:Restriction;
    owl:onProperty <#hasCredentialIssuer>;
    owl:someValuesFrom :TrustedIssuers
] .
```

### Build the WAC rule

And finally, with all that specified, we can write a WAC rule which gives access to agents that could present a credential issued by one of the trusted issuers.

```turtle
@prefix acl: <http://www.w3.org/ns/auth/acl#>  .

<#trustedCredRule> a acl:Authorization;
    acl:agentClass  :credentialedAgents;  
    acl:mode    acl:Read, acl:Write;  
    acl:default <comments/> .
```

## Example Signed University Credential

I started trying to take a few examples [from the VC data model spec](https://www.w3.org/TR/vc-data-model/) such as Examples 1, 2 and 6 in order to map them to N3, where it is easier to see the contexts. Doing that I found a problem with how the Credentials were modeled. Essentially the Signature is placed in an N3 context rather than having the claim being placed in the context. This is explained in [VC Data Model issue 1248](https://github.com/w3c/vc-data-model/issues/1248) and perhaps the deep problem is explained most concisely in [my third comment](https://github.com/w3c/vc-data-model/issues/1248#issuecomment-1691411099).

They are working on [VC Data Model 2.0](https://w3c.github.io/vc-data-model/), and [VC Vocabulary 2.0](https://w3c.github.io/yml2vocab/previews/vcdm/vocabulary.html), so there may be a chance to fix this.
I will get back to examples on this when I am sure I have the model right.

## Client Proof procedures

So how does a client who wants to access a resource with the above rule go and find a credential that satisfies the rule?

After having received a 401 response and having established that <#trustedCredRule> is applicable for the resource and the mode it is attempting to access, the client will find that it needs to understand how the agent class is defined.

For that, the Wallet needs to see if it can find a relation from the Wallet's principal to an issuer via the `<#hasCredentialIssuer>` relation, and then see if the issuer is a trusted one.

Since we are assuming at present that `<#hasCredentialIssuer>` is not a widely used relation, it would be surprising for it to be found in the wallet's QuadStore. 

It may be useful to write out what kind of triples could be found in a Wallet's Quad store


## Server Guard Proof procedure

