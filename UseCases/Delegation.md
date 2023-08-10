# Delegation

We may want to delegate access to a resource to another agent. A person may want to delegate 
1. I can delegate to my mobile phone or solid wallet the job of signing requests for me
2. my personal online data store (POD) may crawl the web, send notifications for me while I am doing other things
3. Delegation example from [§2.3.8 Delegation](https://solid.github.io/authorization-panel/authorization-ucr/#uc-delegation-subset) of the Solid Use Cases Document.

## 1: Full delegation

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

