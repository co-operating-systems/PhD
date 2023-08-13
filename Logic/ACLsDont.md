
# ACL's don't

In a 2009 article [ACLs don’t](https://www.hpl.hp.com/techreports/2009/HPL-2009-20.pdf) Tyler Close explains very clearly the confused deputy problem, and its relation to Access Control Lists with examples taken from the web. The article comes up regularly in discussions about access control, especially whenever ACLs are mentioned.

Since we are extending the term [ACL](ACL.md) to Access Control Logic, and we are furthermore extending it with the says modal logic, and using it with HTTP Signatures, it is not immediately obvious if we are suffering from the problem described by Tyler Close. So we need to investigate.

In part 3 Tyler Close gives 3 examples from the browser world. 
I will look in detail at the first two.

- [ACL's don't](#acls-dont)
  - [Bank Example](#bank-example)
    - [The CSRF example](#the-csrf-example)
      - [Analysis](#analysis)
      - [Analysis Conclusion](#analysis-conclusion)
    - [Bank Example mapped to Solid](#bank-example-mapped-to-solid)
  - [ClickJacking Example](#clickjacking-example)
  - [Conclusion](#conclusion)


## Bank Example 

The first example is the Cross-Site Request Forgery Example (CSRF)

### The CSRF example
 
 The example is loosely based on one brought up in 2004 by Christ Shiflett [Cross-Site Request Forgeries](https://web.archive.org/web/20170221200900/http://shiflett.org/articles/cross-site-request-forgeries), now on the web archive.

 The idea is simple: Alice has logged into a bank and is looking at her account. 
 At that point, Alice receives an e-mail with a link to a page of a Monopoly game, run by Chud. 
 There she clicks on a form she thinks will send some money to another player, but the form is actually pointing to a resource on her Bank's website that will transfer real money to Chud when it receives the POST request. 
 The Bank accepts the request because Alice's browser automatically sends cookies with the request identifying Alice as the user as she is logged in.

Notes:
 * In 2021 Chrome brought in various types of cookies, including ones that would not be transferred on a cross site request. See [SameSite Cookie Changes in February 2020: What You Need to Know](https://blog.chromium.org/2020/02/samesite-cookie-changes-in-february.html)
 * The attack would not work if the form were sent using Javascript Fetch or XMLHttpRequest, because the same site origin policy would prevent the browser from sending the cookies.

#### Analysis

Interestingly what we have is a confusion of who is saying what. 
If the bank website is only looking at the content of the form, 
call that `TransferMoney` then it won't distinguish between

 ```turtle
BankWebPage says Alice says TransferMoney //p1
 ```
 from
 ```
ChudWebPage says Alice says TransferMoney //p2
 ```

Essentially the bank's policy is expressed in [ABLP Logic](ABLP.md) as

```turtle
BankWebPage c:speaksFor Bank .
```

This together with the rule that the Bank quoting Alice - $\text{Bank}|\text{Alice}$ - controls the transfer of money from Alice's account.

todo: for a full account we need to model the browser speaking for Alice speaking from pages with URLs.

 How did that rule come about? 
 Well somewhere the handoff axiom was used by the bank to allow it to handoff statements 

Perhaps because $\text{Bank}$ controls the transfer of any money from all accounts (though that superadmin power should perhaps ever be directly invoked).

todo: write out the proof in detail. The rule would have to be that Bank|Alice controls the transfer of money from Alice's account.
We need a way for the bank to state that the Bank|Alice controls statements about Alice's account (that are valid).

#### Analysis Conclusion

The above initial analysis shows that the bank is failing to take context into account. It could have done that by looking at the `Referer` header which in http indicates to the server where the request is coming from. 
That would have allowed the bank to distinguish the following two principals

$$
\text{Browser}|(\text{BankPage} \land \text{Alice})
$$ 
$$
\text{Browser}|(\text{ChudPage}\land \text{Alice})
$$


The arguments against headers are that they could be manipulated by the user installing a plugin. But if the user altering the browser is an attack vector then nearly anything is possible. 

Adding a unique token to the BankPage just reinforces the proof
that the request is coming from there, but it does not essentially change the logic being used.

So this is a good example of the importance of taking into account who is saying what.

Does it create a problem for AC Logic? Well if we allow
for combined principals like $A|B$ then it looks like we could still boil things down to a simple relation access control fact such as:

```Turtle
[ is conjunction Of (BankPage Alice)] :create </transfer/> .
```

### Bank Example mapped to Solid

How could the bank example work with Solid access control logic, using HTTP Signatures created by a Wallet, and a Banking App?
 We envisage a future where the bank is no longer the sole provider of banking apps, but is open to any number of other apps being used to access the bank's services. 
 This will require a very high degree of trust of the bank in any such app, and so this is unlikely to be the first use case for Solid apps.
This would require the banking apps to be certified by the bank or by trusted neutral third parties. 

We explore this use case in [../UseCases/ClientAuth](../UseCases/ClientAuth#with-proof-of-app-being-used).
It does not seem to create a problem for Web Access Control.

## ClickJacking Example

The clickjacking example is better understood by watching a video illustrating it such as [What is ClickJacking?](https://www.youtube.com/watch?v=_tz0O5-cndE) by Intigriti.

Essentially the trick is to get Alice to think she is clicking on a game button from Chud's website, when she is in fact clicking a button from her Banking website.

The ClickJacking attack is an attack on misleading the user into thinking they are speaking to one agent when in fact they are speaking to another one.
The 1992 paper by Lampson, Abadi, Burrows and Wobber uses principals that are channels to work on this, but I don't quite understand that.

If we could model Alice telling someone to p then 
we can see that the clickjacking attack is an attack relying on confusing these two statements:

```turtle
Alice tells Chudpage to click button
Alice tells Bankpage to click button
```

The answers to this problem could be to make the naming of the pages difficult to guess, which is what Taylor Close suggests, but it could also be
to use [X-Frame-Options](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options) or the newer [CSP: frame-ancestors](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/frame-ancestors) to limit which apps can frame the page. 
Solid Apps should not be framed, so that seems like a good solution. 


## Conclusion

From this preliminary overview, it does not seem that Tyler Close' Confused Deputy problem is something that affects Solid Access Control Logic when used with Http Signatures. 

This is because using HttpSig we sign the request, specifying the target of the request and the mode. As such the signature itself contains the proof of the request. 

I venture the hypothesis that the ACL problem described by Tyler is that it is missing proof objects. 
Those are I guess what those opaque tokens are meant to represent.

So in the compiler example, the problem is that the compiler on receiving the arguments from the caller, should keep track of who told it about those arguments. 
If the User is passing an argument to read a file and one to write to then the compiler should keep that information as
`U says read main.c` and `U says write to log.txt`. 
Then would allow the compiler to verify that information immediately, resulting in a proof object, or it could do it later because it has all the information to do so.

The problem arises because the modality of who said what is
ignored in simple ACL systems. 
If those are not ignored, then the problem does not arrive.
The capabilities are just what one would call in intuitionistic
logic a proof object. 

Proof objects need not be opaque. 
They can be signed statements with semantics.
