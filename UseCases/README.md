In this section we explore important use cases of access control for Solid, and the type of reasoning that would be needed on the client and on the server for those to succeed.

* [Basic.md](Basic.md) looks at the basic authentication use cases using a public key and then extending it to a WebId for the purpose of making it easier to maintain access control rules
* [Foaf.md](Foaf.md) explores the important social networking use cases that launched interest in this whole enterprise to start with. 
  We look at a simple friend network, followed by a friend of a friend of a friend. 
  This last one brings up an unavoidable scale problem: assume each person has 100 non-overlapping friends, we would end up creating a social network 100*100*100=1 000 000=1 million people large
* [ClientAuth.md](ClientAuth.md) looks at the problem of limiting Apps.
   We distinguish two types of limitations from the client side and the server side. 
   We show how these map to ABNL logic.
* [Delegation.md](Delegation.md) looks at Delegation use cases
* [WoN.md](WoN.md) the certificate based examples will often require a Web of Nations infrastructure. We look at examples of these.