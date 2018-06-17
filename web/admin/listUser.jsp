<%@ page contentType="text/html; charset=UTF-8" language="java"
         pageEncoding="UTF-8" import="java.util.*" %>

<%@ include file="../include/adminHeader.jsp"%>
<%@ include file="../include/adminNavigator.jsp"%>

<title>用户管理</title>

<div class="workingArea">
    <h1 class="label label-info">用户管理</h1>
    <br/>
    <br/>

    <div class="listDataTableDiv">
        <table class="table table-striped table-bordered table-hover  table-condensed">
            <therd>
                <tr class="success">
                    <td>ID</td>
                    <td>用户名称</td>
                </tr>
            </therd>

            <tbody>
                <c:forEach items="${us}" var="u">
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.name}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="pageDiv">
        <%@ include file="../include/adminPage.jsp"%>
    </div>
</div>

<%@ include file="../include/adminFooter.jsp"%>
