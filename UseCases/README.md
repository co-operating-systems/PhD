In this section we explore important use cases of access control for Solid, and the type of reasoning that would be needed on the client and on the server for those to succeed. The idea is to see if one can think of intuitive reasoning strategies for individual cases, and then to see if there is a way to generalize those.

* [Basic](Basic.md) looks at the basic authentication use cases using a public key and then extending it to a WebId to make it easier to maintain access control rules
* [Foaf](Foaf.md) explores the important social networking use cases that launched interest in this whole enterprise to start with. 
  We look at a simple friend network, followed by a friend of a friend of a friend. 
  This last one brings up an unavoidable scale problem: assume each person has 100 non-overlapping friends, we would end up creating a social network 100*100*100=1 000 000=1 million people large
* [Client Auth](ClientAuth.md) looks at the problem of limiting Apps.
   We distinguish two types of limitations from the client side and the server side. 
   We show how these map to ABNL logic.
* [Delegation](Delegation.md) looks at Delegation use cases
* [Deepak Garg's Phd](DeepakGargPhd.md) Deepak Garg's 2009 PhD built a detailed extension of the says logic for access control purposes with full proof procedures. We want to see how far we can adatpt those to the Web.
* [Verifiable Credentials](VerifiableCredentials.md) looks at how one can use W3C Verifiable Credentials standards for access control
* [WoN.md](WoN.md) the certificate-based examples will often require a Web of Nations infrastructure. We look at examples of these.