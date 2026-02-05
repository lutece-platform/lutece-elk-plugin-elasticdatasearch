<jsp:useBean id="manageelasticdatasearchFieldsearch" scope="session" class="fr.paris.lutece.plugins.elasticdatasearch.web.FieldsearchJspBean" />
<% String strContent = manageelasticdatasearchFieldsearch.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
