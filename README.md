# pf-store-query-params-adapter-selector

- pickup the idp or IdpAdapterId parameter and return its value from the selector
- store parameters from incoming request in SessionStateSupport

The stored parameter (e.g. a `client_id`) can be used in e.g. browser SSO protocol customizations, such as a modification of the SAML AuthnRequest:

```
#sessionStateSupport = new org.sourceid.saml20.adapter.state.SessionStateSupport(),
#value = #sessionStateSupport.getAttribute("client_id", #HttpServletRequest, #HttpServletResponse) == null ? 'defaultappid' : #sessionStateSupport.removeAttribute("client_id", #HttpServletRequest, #HttpServletResponse),
#AuthnRequestDocument.getAuthnRequest().addNewRequestedAuthnContext().addAuthnContextClassRef(#value)
```
