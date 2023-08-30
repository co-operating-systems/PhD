# Progression Review

The [2nd year report](https://co-operating.systems/2019/04/01/) in §4.2 laid out a road map of work that remained to be done. This section reviews the progress made on that road map. 

- [Progression Review](#progression-review)
- [Done](#done)
  - [1. Use Cases](#1-use-cases)
  - [2. Access Control Logic](#2-access-control-logic)
  - [3. Implementation](#3-implementation)
- [Remaining to be done](#remaining-to-be-done)

# Done

In the past four years, since the publication of the [2nd Progress review](https://co-operating.systems/2019/04/01/) in April 2019, I have worked in the following Ph.D. related projects:

* MIT Category Theory and Data Startup [Conexus.com](https://conexus.com/) with Ryan Wisnesky building on work by [David Spivak](https://math.mit.edu/~dspivak/). This led me to write up a sketch of a paper on Functorial RDF and give a presentation at SemWeb Pro ([see documents](https://co-operating.systems/2020/11/));
* EU project [Solid-Control](https://nlnet.nl/project/SolidControl/) to work on access control and building of a Solid Server in Scala;
* Consulting on Linked Data, Access Control and Big data for [Imec](https://en.wikipedia.org/wiki/IMEC) and their EU projects such as the [Vlaamsee Smart Data Spaces](https://www.vlaanderen.be/digitaal-vlaanderen/onze-oplossingen/vlaamse-smart-data-space) project;
* EU project [Solid-Wallet](https://nlnet.nl/project/SolidWallet/) to work on the client side of Access Control. This is ongoing.

The above projects allowed me to work on the following topics.

## 1. Use Cases

Working with the [Solid Authorization Panel](https://github.com/solid/authorization-panel), we put together a [Use Cases and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/) document in 2020.  This document covered some key use cases that needed to be supported at a high level of description.

Recently, I started developing these use cases by describing each one in detail and proposing OWL-based extensions of the current [Web Access Control](https://solidproject.org/TR/wac) as proposed in the 2nd year report. See [UseCases](UseCases/README.md) for the current state of this work.

Many important use cases require Proof Carrying Authorization, where the client sends a proof sketch showing how his credentials link to the resource's rules.
So in the Use [UseCases](UsesCases/README.md) section, we sketch in human language what the client would need to produce and what the server would need to verify.

## 2. Access Control Logic

To support the use case reasoning required by Proof Carrying Authorization we needed to look at the logic of access control in more detail. I have been filling out the [Logics](Logic/) section as needed when filling in the examples. 
This goes over the research from the 2nd year report but brings it in closer contact with the semantic web via [N3](https://github.com/w3c/N3/) and [RDF Surfaces](https://github.com/w3c-cg/rdfsurfaces/).

## 3. Implementation

To test out these ideas it was important to have an implementation of a Solid Server. With the help of the EU project [Solid-Control](https://github.com/co-operating-systems/solid-control) I was able to build a Solid server in Scala on top of the [Akka Actor Framework](https://akka.io/). Instead of the complicated [Solid-OIDC](https://solidproject.org/TR/oidc) authentication protocol, I decided to focus on the much more efficient [HTTP Message Signatures](https://datatracker.ietf.org/doc/draft-ietf-httpbis-message-signatures/) which is in the last stages of acceptance by the IETF. For a recent presentation of this in action see the video demonstration:

[![HttpSig demo for big data 2023-06-08](https://github.com/co-operating-systems/solid-control/blob/main/milestones/W2/Tweet-2023-06-08.png?raw=true)](https://twitter.com/bblfish/status/1666547828506742788)

# Remaining to be done


As part of the Solid-Wallet EU project, I have to build a client wallet with knowledge of the credentials of the user and his social network graph, and that can reason through an access-control rule, find linked data that can prove the rule, and send a proof to the server that will then need to verify that it matches the required rule. 

There remain 5 to 6 milestones for this work, which will require developing client and server-side libraries and building or finding a reasoning engine that can provide the required proofs.

The most uncertain part, i.e. the most advanced research part of this, is the reasoning engine and logic. [Deepak Garg's Says logic](Logic/Says.md#deepak-gargs-2009-bl-logic) is a good up-to-date (2009) candidate to try to map to the Prolog-based [N3 EYE rules](https://eyereasoner.github.io/eye/#eye-reasoning). 
EYE is an implementation of an [N3 logic](https://github.com/w3c/n3) reasoning engine, an earlier version of which was used by Tim Berners-Lee, and others, for experiments on Proof Carrying Authorization - see the 2005 paper [Towards a Policy Aware Web](https://www.researchgate.net/profile/James-Hendler/publication/228415906_W9_The_Semantic_Web_and_Policy_Workshop_SWPW/links/0deec527a72c5a0968000000/W9-The-Semantic-Web-and-Policy-Workshop-SWPW.pdf#page=111).

We then also need to map this to a langauge so that it can be sent over HTTP. An early idea was to develop a [Wac-Hint HTTP header](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/http/auth/Auth.md#creating-a-new-header-wac-hint) that would give a path for the reasoning engine to follow. Given that Deepak Garg's logic is expressed in sequent calculus and that can be mapped to a Category where turnstiles map to arrows, this may be a way to formalize a proof as a both through graph space... An ideal option if proofs get too large, would be [p2p HTTP](https://github.com/w3c/architecture/issues/14) which would allow RESTful reference to a proof to be sent to the server.

I then should count 3 months to write up the results in their final form, taking them over from the wiki to a latex output.

This is a lot of work to do. Working out the link of the PCA with Linked Data and the proof exchange is the most needed but also the most uncertain part.










