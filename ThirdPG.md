# Progression Review

The [2nd year report](https://co-operating.systems/2019/04/01/) in §4.2 laid out a road map of work that remained to be done. This section reviews the progress made on that road map. 

- [Progression Review](#progression-review)
- [Done](#done)
  - [1. Use Cases](#1-use-cases)
  - [2. Access Control Logic](#2-access-control-logic)
  - [3. Implementation](#3-implementation)
  - [4. Standardisation](#4-standardisation)
  - [5. Problems](#5-problems)
- [Remaining](#remaining)
  - [1. Solid Wallet](#1-solid-wallet)
  - [2. Logic and Reasoning Engine](#2-logic-and-reasoning-engine)
  - [3. Proof Carrying Authorization for Solid](#3-proof-carrying-authorization-for-solid)
  - [4. Proof of HttpSig protocol](#4-proof-of-httpsig-protocol)
  - [5. Writing up the Thesis](#5-writing-up-the-thesis)
  - [6. Solid Working Group](#6-solid-working-group)

# Done

In the past four years, since the publication of the [2nd Progress review](https://co-operating.systems/2019/04/01/) in April 2019, I have worked on the following Ph.D.-related projects:

* MIT Category Theory and Data Startup [Conexus.com](https://conexus.com/) with Ryan Wisnesky building on work by [David Spivak](https://math.mit.edu/~dspivak/). This led me to write up a sketch of a paper on Functorial RDF and give a presentation at SemWeb Pro ([see documents](https://co-operating.systems/2020/11/));
* EU project [Solid-Control](https://nlnet.nl/project/SolidControl/) to work on access control and building of a Solid Server in Scala;
* Consulting on Linked Data, Access Control and Big data for [Imec](https://en.wikipedia.org/wiki/IMEC) and their EU projects such as the [Vlaamsee Smart Data Spaces](https://www.vlaanderen.be/digitaal-vlaanderen/onze-oplossingen/vlaamse-smart-data-space) project;
* EU project [Solid-Wallet](https://nlnet.nl/project/SolidWallet/) to work on the client side of Access Control. This is ongoing.

The above projects allowed me to work on the following topics.

## 1. Use Cases

Working with the [Solid Authorization Panel](https://github.com/solid/authorization-panel), we put together a [Use Cases and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/) document in 2020.  This document covered key use cases that needed to be supported at a high level of description.

Recently, I started developing and extending these use cases by describing each one in detail, proposing OWL-based extensions of the current [Web Access Control spec](https://solidproject.org/TR/wac) as proposed in the 2nd year report. See [UseCases](https://github.com/co-operating-systems/PhD/blob/3rdPG/UseCases/README.md) for the current state of this work.

Many important use cases require Proof Carrying Authorization, where the client sends a proof sketch showing how his credentials link to the resource's rules.
So in the Use [UseCases](https://github.com/co-operating-systems/PhD/blob/3rdPG/UsesCases/README.md) section, we sketch in human language what the client would need to produce and what the server would need to verify.

## 2. Access Control Logic

To support the use case reasoning required by Proof Carrying Authorization we needed to look at the logic of access control in more detail. I have been filling out the [Logics](https://github.com/co-operating-systems/PhD/blob/3rdPG/Logic/) section as needed when filling in the examples. 
This goes over the research from the 2nd year report but brings it in closer contact with the semantic web via [N3](https://github.com/w3c/N3/) and [RDF Surfaces](https://github.com/w3c-cg/rdfsurfaces/).

## 3. Implementation

To test out these ideas it was important to have an implementation of a Solid Server. With the help of the EU project [Solid-Control](https://github.com/co-operating-systems/solid-control) I was able to build a Solid server in Scala on top of the [Akka Actor Framework](https://akka.io/). Instead of the complicated [Solid-OIDC](https://solidproject.org/TR/oidc) authentication protocol, I decided to focus on the much more efficient [HTTP Message Signatures](https://datatracker.ietf.org/doc/draft-ietf-httpbis-message-signatures/) which is in the last stages of acceptance by the IETF. For a recent presentation of this in action see the video demonstration:

[![HttpSig demo for big data 2023-06-08](https://github.com/co-operating-systems/solid-control/blob/main/milestones/W2/Tweet-2023-06-08.png?raw=true)](https://twitter.com/bblfish/status/1666547828506742788)

## 4. Standardisation

In implementing the [HTTP Message Signatures](https://datatracker.ietf.org/doc/draft-ietf-httpbis-message-signatures/) I tested every example and provided a lot of feedback, resulting in my name being listed in the RFC.

Linking the IETF work to Solid is being documented in the [HttpSig Authentication Spec](https://github.com/solid/authentication-panel/blob/main/proposals/HttpSignature.md) with work starting on updating the spec and [translating it to HTML](https://co-operating.systems/2023/09/10/httpsig.html).

## 5. Problems 

I was hoping to be able to use the Verifiable Credentials suite of specs from W3C (either [VC Data Model 1.1](https://www.w3.org/TR/vc-data-model/) or [VC Data Model 2.0 Draft](https://w3c.github.io/vc-data-model/#example-a-simple-example-of-a-verifiable-credential)), to develop interesting examples of age verification, etc... but on mapping examples from those specs from JsonLD to N3 I found that the model was deeply flawed as [explained in issue 1248 of the vc-data-model](https://github.com/w3c/vc-data-model/issues/1248#issuecomment-1697508577).
A potential solution might be to enhance JsonLD [as I propose in issue 817 of json-ld.org](https://github.com/json-ld/json-ld.org/issues/817) to allow a @shift operator that could shift the default graph. The initial reaction to deny the importance of the problem and to shift it to the future does not bode well, especially given that I [made a similar point 7 years ago](https://lists.w3.org/Archives/Public/public-webpayments/2016Jan/0020.html) when most of the work was still to be done.


# Remaining 

## 1. Solid Wallet

The Solid-Wallet project for the EU which I put together, requires me to build a client wallet that can sign HTTP headers for Solid Apps. To do this the wallet will need to read the resource's Web Access Control Rules, and with knowledge of the user's credentials and social network, find a (sketch of a) proof that can show the guard that the signature can be linked to the description in the rule. Good examples of this can be found in [the FOAF use cases](https://github.com/co-operating-systems/PhD/blob/3rdPG/UseCases/Foaf.md)

There remain 5 to 6 milestones for this work, which will require developing client and server-side libraries and building or finding a reasoning engine that can provide the required proofs, as well as developing a user interface to launch apps and sign headers for clients.

This is part of the proof of concept of the theoretical side of the Ph.D.

## 2. Logic and Reasoning Engine

The most uncertain part, i.e. the most advanced research part of this, is the reasoning engine and logic. [Deepak Garg's Says logic](https://github.com/co-operating-systems/PhD/blob/3rdPG/Logic/Says.md#deepak-gargs-2009-bl-logic) which he developed in his 2009 Ph.D. thesis, is a good up-to-date (2009) candidate to try to map to the Prolog-based [N3 EYE rules](https://eyereasoner.github.io/eye/#eye-reasoning). As was discussed in the 2nd year report it maps very well to a notion of the web as a system of document acts, and it also fits in very well with the graph structure of RDF, and especially nicely with N3 where we can express that 

```Turtle
:joe :says { :joe :knows :mary } .
```

EYE is an implementation of an [N3 logic](https://github.com/w3c/n3) reasoning engine, an earlier version of which was used by Tim Berners-Lee, and others, for experiments on Proof Carrying Authorization - see the 2005 paper [Towards a Policy Aware Web](https://www.researchgate.net/profile/James-Hendler/publication/228415906_W9_The_Semantic_Web_and_Policy_Workshop_SWPW/links/0deec527a72c5a0968000000/W9-The-Semantic-Web-and-Policy-Workshop-SWPW.pdf#page=111).

## 3. Proof Carrying Authorization for Solid

We then also need to map this to a language so that it can be sent over HTTP. An early idea was to develop a [Wac-Hint HTTP header](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/http/auth/Auth.md#creating-a-new-header-wac-hint) that would give a path for the reasoning engine to follow. Given that Deepak Garg's logic is expressed in sequent calculus and that can be mapped to a Category where turnstiles map to arrows, this may be a way to formalize a proof as a both through graph space... An ideal option if proofs get too large, would be [p2p HTTP](https://github.com/w3c/architecture/issues/14) which would allow RESTful reference to a proof to be sent to the server.

## 4. Proof of HttpSig protocol

A Recent Master's thesis looked in detail at the Solid OIDC protocol using protocol verification tools and found security flaws.
These are summarised in the paper [Assessing the Solid Protocol in Relation to Security & Privacy Obligations](https://arxiv.org/abs/2210.08270). 
The HTTP Message Signature protocol is much simpler than the OIDC protocol at its base (when only keys need to be verified) and so should constitute a small subset of the OIDC protocol. 
The addition of the declarative proof of authorization will add some complexity to the proof though.


## 5. Writing up the Thesis

Developing the examples further with a version of `says` logic, implementing them in N3, and working with proof-carrying authorization will reveal limitations in each of these frameworks that can then be documented in the thesis, and hopefully overcome.

I then should count 3 months to write up the results in their final form, taking them over from the wiki to a latex output.

## 6. Solid Working Group

The Solid Team is putting forward a proposal for a W3C Solid Working Group ([see charter](https://solid.github.io/solid-wg-charter/charter/)) that will be discussed at [TPAC 2023 in Seville](https://www.w3.org/2023/09/TPAC/), and should get started perhaps in 2024. 
This will be a good reason for developing this work further.









