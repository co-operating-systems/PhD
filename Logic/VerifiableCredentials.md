![Person showing a passport to a border guard questioning the resemblance to the picture](https://www.vmcdn.ca/f/files/victoriatimescolonist/json/2022/06/web1_raeside_passport_web.jpg;w=960;h=640;bgcolor=000000)

- [VC data Model](#vc-data-model)
  - [Credentials](#credentials)
  - [Verifiable Presentations](#verifiable-presentations)


# VC data Model

The [W3C Verifiable Credentials Data Model](https://www.w3.org/TR/vc-data-model/) is the core of the Verifiable Credentials standards and is itself an example of something that should be explained in terms of the [says logic](Says.md) as we will see

## Credentials

The intended structure of a Credential is clearly illustrated in Figure 6 of the VC Data Model document shown here. 
(We say intended, because there seems to be a problem in the jsonld context as explained in [issue 1248](https://github.com/w3c/vc-data-model/issues/1248))

![Figure 6 Information graphs associated with a basic verifiable credential.](https://camo.githubusercontent.com/d37bdf92cae6a0faeb4d9c25419d1e3b8c8ba74d68a64040f36d155b090081ee/68747470733a2f2f7777772e77332e6f72672f54522f76632d646174612d6d6f64656c2f6469616772616d732f63726564656e7469616c2d67726170682e737667)

The diagram shows 3 claims, i.e. sets of relations that we can think of as being placed on surfaces, following an idea from Pat Hayes, that he took from Peirce, and which is now being worked on in the [RDF Surfaces CG](https://w3c-cg.github.io/rdfsurfaces/).

1. The yellow surface is the asserted **claim**, in this case, that Pat is an alumnus of "Example University".  
2. The pink surface on which the yellow claim is "said" by the issuer - the University - at a certain date is the **credential**. 
3. The green surface contains the credential **proof**.

The Yellow surface is the claim that presumably the Guard needs to know as part of an access control decision. 
 But to start with, neither the Pink nor the yellow surface data should be taken as true or false by the Guard looking at it. Only if the signature is verified, can the top surface in pink be taken to be true. 

In the [says logic](Says.md) this would be expressed as something like this:

$$
\text{ UniKey } \text{ says } \text{ Uni } \text{ says } \text{ PaU }
$$
where $\text{ PaU } \equiv \text{ Pat alumnusOf Uni }$

This suggests that the pink and yellow surfaces should be drawn as placed onto the green surface, which follows by associativity of the `says` relation:

$$
\text{UniKey} \text{ says } ( \text{ Uni } \text{ says } \text{ PaU } )
$$

To be precise, we should perhaps start with The Guard initially receiving the following claim from some `connAgent`,
ie the agent at the other end of a connection.  

$$
\text{ connAgent } \text{ says } (\text{ UniKey } \text{ says } ( \text{ Uni } \text{ says } \text{ PaU} ))
$$

From the Guard's point of view, the only fact that exists at that point in time, is that the `connAgent` said that thing (that the `UniKey` said...) 
None of the embedded statements are to be taken as true or false at that point by the Guard. 
They are quoted sayings, and their truth value is yet to be decided.

Yet, without needing to trust the `connAgent` at all, the Guard can verify the embedded claim made by the UniKey using cryptographic signatures so that it can conclude to the truth of the pink surface.  
That is because the Guard trusts the University key to speakFor the University.
So he concludes to the truth of the Pink surface

$$
\text{ Uni } \text{ says } \text{ PaU } 
$$

Again here, the Guard has a fact that does not assert or deny that `Pat` is an alumnus of `Uni``, only that the `Uni` said so. 
So we are now on the pink surface where the Guard does not yet know if the yellow surface is true or false. 
If the Guard trusts the issuer about claims it makes regarding its own members - which seems like a reasonable assumption - then the Guard can proceed to believe that `Pat` is an alumnus of that university. Perhaps that is what is needed to be proven to satisfy an access control rule.

All that makes logical sense. If we wanted to write the above official diagram in N3, after adjusting for placing all surfaces on the green surface, and making that green surface the default graph, we would have something like the following. 


```Turtle
@prefix sec:     <https://w3id.org/security#> .
@prefix cred:    <https://www.w3.org/2018/credentials#> .
@prefix eg:      <https://example.org/examples#> .
@prefix xsd:     <http://www.w3.org/2001/XMLSchema#> .
@prefix schema: <http://schema.org/>.

{   
<http://example.edu/credentials/1872>
    cred:issuer <https://example.edu/issuers/565049> ;
    a   eg:AlumniCredential ,  cred:VerifiableCredential ;
    cred:issuanceDate "2010-01-01T19:23:24Z"^^xsd:dateTime ;
    cred:credentialSubject
        <did:example:ebfeb1f712ebc6f1c276e12ec21> ;
    cred:claim { 
    <did:example:ebfeb1f712ebc6f1c276e12ec21>
         schema:alumniOf [ id <did:example:c276e12ec21ebfeb1f712ebc6f1> 
               schema:name "Exemple d'Université"@fr ,   "Example University"@en 
         ] 
   }
 } sec:proofObject [  a sec:RsaSignature2018 ;
     dc:created "2017-06-18T21:19:10Z"^^xsd:dateTime ;
     sec:jws "eyJhbGciOiJSUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..TCYt5XsITJX1CxPCT8yAV-TVkIEq_PbChOMqsLfRoPsnsgw5WEuts01mq-pQy7UJiN5mgRxD-WUcX16dUEMGlv50aqzpqh4Qktb3rk-BuQy72IFLOqV0G_zS245-kronKb78cPN25DGlcTwLtjPAYuNzVBAh4vGHSrQyHUdBBPM" ;
      sec:proofPurpose sec:assertionMethod ;
      sec:verificationMethod
               <https://example.edu/issuers/565049#key-1> 
    ]  .
```

The green surface disappears, because, in the language of [RDF Surfaces](https://w3c-cg.github.io/rdfsurfaces/), if the top surface is positive, then we can take it to be the default surface, i.e. in NQuads language we have the default graph.

## Verifiable Presentations

![Dilbert Presentation](https://bblfish.net/tmp/2023/08/dilbert.jpg)

todo