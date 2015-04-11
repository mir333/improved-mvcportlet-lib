#Imporved Liferay MVCPortlet

This small library extends Liferay MVCPortlet with the intention to provide a cleaner MVC framework.

Original Liferay MVCPortlet framework mimics quite heavenly the behaviour of struts and is more ViewAction then proper MVC. In MVCPortlet is the controller heavily bypassed and the JSP (View) do a lot of data loading on their own.

This improved library keeps all the good features present in MVCPortlet and strengthens the position of the controller as the only point of interaction with business logic. This leads to a clean separation between View and Controller.

Features:
+ The call of render phase was extended to call a setModel method. This method is used to populate the model that is then forwarded to view.
+ User can define oven "setModel" method for different view. The methods are called as custom action method in original MVCPortlet
+ The past values are stored as request attributes to EL expression can be easily used.
+ Serve resource method may be called the same way as custom action methods in original MVCPortlet. The method name must be the same as the *resourceId*
