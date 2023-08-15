
# ACL's don't

In a 2009 article, [ACLs don’t](https://www.hpl.hp.com/techreports/2009/HPL-2009-20.pdf), Tyler Close explains very clearly the confused deputy problem and its relation to Access Control Lists with examples taken from the web. 
The article regularly comes up in discussions about access control, especially whenever ACLs are mentioned. 
The point of the article is taken to be that ACLs cannot work, and so one needs to use Capability systems.

Since we are extending the term ACL to [Access Control Logic](./ACL.md) and we are furthermore extending it with the says modal logic, and using it with [HTTP Signatures](https://github.com/bblfish/authentication-panel/blob/sigUpdate/proposals/HttpSig/HttpSignature.md) - which is a lot more flexible than WebID-TLS - it is not immediately obvious if we are suffering from the problem described by Tyler Close. 
So we need to investigate.

In part 3, Tyler Close gives 3 examples from the web to illustrate the problem.
I will look in detail at the first two, as the last one is not that relevant.

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
 At that point, Alice receives an e-mail with a link to a page of a Monopoly game run by Chud. 
 There she clicks on a form she thinks will send some money to another player, but the form is actually pointing to a resource on her Bank's website that will transfer real money to Chud when it receives the POST request. 
 The Bank accepts the request because Alice's browser automatically sends cookies with the request identifying Alice as the user as she is logged in.

Notes:
 * In 2021, Chrome brought in various types of cookies, including ones that would not be transferred on a cross-site request. See [SameSite Cookie Changes in February 2020: What You Need to Know](https://blog.chromium.org/2020/02/samesite-cookie-changes-in-february.html)
 * The attack would not work if the form were sent using Javascript Fetch or XMLHttpRequest because the same site origin policy would prevent the browser from sending the cookies.

#### Analysis

Interestingly what we have is a confusion of who is saying what. 
If the bank website is only looking at the content of the form, 
call that `TransferMoney`, then it won't distinguish between

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

which needs to be conjoined with the rule that the Bank quoting Alice - $\text{Bank}|\text{Alice}$ - controls the money transfer from Alice's account.

todo: for a full account, we need to model the browser speaking for Alice speaking from pages with URLs.

Perhaps because $\text{Bank}$ controls the transfer of any money from all accounts (though that super-admin power should perhaps ever be directly invoked).

todo: write out the proof in detail. The rule would have to be that Bank|Alice controls money transfers from Alice's account.
We need a way for the bank to state that the Bank|Alice controls statements about Alice's account (that are valid).

The bank's reasoning was modeled in N3 very nicely by Dan Connolly in his 2009 note [Mashup Speech Acts](https://www.w3.org/2001/tag/dj9/mashup.html). The Bank's reasoning mistake is to only use the cookie as identification of who speaks. That
works as it should. But the user is not speaking directly to the bank, but via a web page served by a browser. So the authentication rule needs to take into account who is quoting the user. Is it the BankPage or is it ChudPage? 

#### Analysis Conclusion

The above initial analysis shows that the bank is failing to take context into account. It could have done that by looking at the `Referer` header, which in HTTP indicates to the server where the request is coming from. 
That would have allowed the bank to distinguish the following two principals

$$
(1a) \text{ Browser}|(\text{BankPage} | \text{Alice}) 
$$ 
$$
(2a) \text{ Browser}|(\text{ChudPage}| \text{Alice})
$$

If Alice correctly authenticated to the bank with the Browser over an HTTPS connection and with the right cookies being sent, then her choice of Browser is also proof that she trusts that browser, so that what that Browser says is true. So the Browser is in control. Hence we can simplify the above Principals respectively to

$$
(1b) \text{ BankPage} | \text{Alice}
$$ 
$$
(2b) \text{ChudPage}| \text{Alice}
$$

But the rule for the bank should be that only (1b) is allowed to act on the transfer of money, not (2b), because only 
$$
\text{ BankPage } \text{ speaksFor } \text{Bank}
$$
but we don't have that $\text{ ChudPage } \text{ speaksFor } \text{Bank}$.

The arguments against using headers from the "ACL's don't" paper are that those could be manipulated by the user installing a plugin. But if the user altering the browser is an attack vector then nearly anything is possible, nothing can be trusted, neither cookies, nor passwords, nor anything.  

Adding a unique token to the BankPage does help, but just because it reinforces the proof that the request is coming from $\text{BankPage}$, it does not essentially change the logic being used.

So this is a good example of the importance of considering who is saying what.

Does it create a problem for AC Logic? Well, if we allow for combined principals like $A|B$, then it looks like we could still boil things down to a simple relation access control fact such as:

```Turtle
[ is speaksFor of (BankPage Alice)] :create </transfer/> .
```

### Bank Example mapped to Solid

How could the bank example work with Solid access control logic, using HTTP Signatures created by a Wallet and a Banking App?
 We envisage a future where the bank is no longer the sole provider of banking apps but is open to any other apps being used to access the bank's services. 
 This will require a very high degree of trust of the bank in any such app, so this is unlikely to be the first use case for Solid apps.
This would require the banking apps to be certified by the bank or by trusted, neutral third parties. 

We explore this use case in [../UseCases/ClientAuth](../UseCases/ClientAuth#with-proof-of-app-being-used).
It does not seem to create a problem for Web Access Control.

## ClickJacking Example

The clickjacking example is better understood by watching a video illustrating it, such as [What is ClickJacking?](https://www.youtube.com/watch?v=_tz0O5-cndE) by Intigriti.

Essentially the trick is to get Alice to think she is clicking on a game button from Chud's website when she is in fact, clicking a button from her Banking website.

The ClickJacking attack is an attack on misleading the user into thinking they are speaking to one agent when in fact, they are speaking to another one.
The 1992 paper by Lampson, Abadi, Burrows, and Wobber uses principals that are channels to work on this, but I don't quite understand that.

If we could model Alice telling someone to p, then we can see that the clickjacking attack is an attack relying on confusing these two statements:

```Turtle
Alice tells Browser tells ChudPage to click on button b.
Alice tells Browser tells Bank to click on button b.
```

The answer to this problem could be to make the naming of the pages difficult to guess, which is what Taylor Close suggests, but it could also be to use [X-Frame-Options](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options) or the newer [CSP: frame-ancestors](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/frame-ancestors) to limit which apps can frame the page. 
Solid Apps should not be framed, so that seems like a good solution. 

## Conclusion

From this preliminary overview, it does not seem that Tyler Close' Confused Deputy problem affects Solid Access Control Logic when used with HTTP Signatures. 

This is because, using HttpSig we sign the request, specifying the target of the request and the mode. As such, the signature itself contains the proof of the request. 

I venture the hypothesis that the ACL problem described by Tyler is that it is missing proof objects. 
Those are, I guess, what those opaque tokens are meant to represent.

So in the compiler example, the problem is that the compiler, on receiving the arguments from the caller, should keep track of who told it about those arguments. 
If the User passes an argument to read a file and one to write to, then the compiler should keep that information as `U says read main.c` and `U says write to log.txt`. 
Then would allow the compiler to verify that information immediately, resulting in a proof object, or it could do it later because it has all the information to do so.

The problem arises because the modality of who said what is ignored in simple ACL systems. 
If those are not ignored, then the problem does not manifest itself.
The capabilities are just what one would call, in intuitionistic logic, a proof object. 

Note that Proof objects need not be opaque. 
They can be signed statements with semantics.
