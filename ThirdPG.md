# Progression Review

The [2nd year report](https://co-operating.systems/2019/04/01/) in §4.2 laid out a road map of work that remained to be done. This section reviews the progress made on that road map. 

- [Progression Review](#progression-review)
  - [1. Use Cases](#1-use-cases)
  - [2. Access Control Logic](#2-access-control-logic)
  - [3. Implementation](#3-implementation)
  - [4. Remaining to be done](#4-remaining-to-be-done)


## 1. Use Cases

Working with the [Solid Authorization Panel](https://github.com/solid/authorization-panel), we put together a [Use Cases and Requirements for Authorization in Solid](https://solid.github.io/authorization-panel/authorization-ucr/) document in 2020.  This document covered some key use cases that needed to be supported at a high level of description.

Recently, I started developing these use cases by describing each one in detail and proposing OWL-based extensions of the current [Web Access Control](https://solidproject.org/TR/wac) as proposed in the 2nd year report. See [UseCases](UseCases/README.md) for the current state of this work.

Many important use cases require Proof Carrying Authorization, where the client sends a proof sketch showing how his credentials link to the resource's rules.
So in the Use [UseCases](UsesCases/README.md) section, we sketch in human language what the client would need to produce and what the server would need to verify.

## 2. Access Control Logic

To support the use case reasoning required by Proof Carrying Authorization we needed to look at the logic of access control in more detail. I have been filling out the [Logics](Logic/) section as needed when filling in the examples. 
This goes over the research from the 2nd year report but brings it in closer contact with the semantic web via [N3](https://github.com/w3c/N3/) and [RDF Surfaces](https://github.com/w3c-cg/rdfsurfaces/).

## 3. Implementation

To test out these ideas it was important to have an implementation of a Solid Server. With the help of the EU project [Solid-Control](https://github.com/co-operating-systems/solid-control) I was able to build a Solid server in Scala on top of the [Akka Actor Framework](https://akka.io/). Instead of the complicated [Solid-OIDC](https://solidproject.org/TR/oidc) authentication protocol, I decided to focus on the much more efficient [HTTP Message Signatures](https://datatracker.ietf.org/doc/draft-ietf-httpbis-message-signatures/) which is in the last stages of acceptance by the IETF. For a recent presentation of this in action see the video:
 [](https://twitter.com/bblfish/status/1666547828506742788)

[![HttpSig demo for big data 2023-06-08](https://github.com/co-operating-systems/solid-control/blob/main/milestones/W2/Tweet-2023-06-08.png?raw=true)](https://twitter.com/bblfish/status/1666547828506742788)

## 4. Remaining to be done

As part of the Solid-Wallet EU project, I have to build a client wallet with knowledge of the credentials of the user and his social network graph, and that can reason through an access-control rule, find linked data that can prove the rule, and send a proof to the server that will then need to verify that it matches the required rule.  

There remain 5 to 6 milestones for this work, which will require developing client and server-side libraries and building or finding a reasoning engine that can provide the required proofs. This will enable 

The most uncertain part of this is the reasoning engine and logic. [Deepak Garg's Says logic](Logic/Says.md#deepak-gargs-2009-bl-logic) is a good up-to-date (2009) candidate to try to map to the Prolog-based [N3 EYE](https://eyereasoner.github.io/eye/#eye-reasoning) reasoning engine rules.


We then also need to map this so that it can be sent over HTTP. An early idea was to develop a [Wac-Hint HTTP header](https://github.com/co-operating-systems/Reactive-SoLiD/blob/master/src/main/scala/run/cosy/http/auth/Auth.md#creating-a-new-header-wac-hint) that would give a path for the reasoning engine to follow. Given that Deepak Garg's logic is expressed in sequent calculus and that can be mapped to a Category where turnstiles map to arrows, this may be a way to formalize a proof as a both through graph space...









