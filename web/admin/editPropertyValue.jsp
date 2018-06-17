<%@ page contentType="text/html; charset=UTF-8" language="java"
         pageEncoding="UTF-8" import="java.util.*" %>
<html>
<head>

</head>
<body>
<%@ include file="../include/adminHeader.jsp"%>
<%@ include file="../include/adminNavigator.jsp"%>

<title>编辑产品属性值</title>

<script>
    $(function () {
        $("input.pvValue").keyup(function () {
            var pvid = $(this).attr("pvid");
            var page = "admin_product_updatePropertyValue";
            var value = $(this).val();
            var parentSpan = $(this).parent("span");
            parentSpan.css("border","1px solid yellow");
            $.post(
                page,
                {"value":value,"pvid":pvid},
                function (result) {
                    if ("success"==result) {
                        parentSpan.css("border","1px solid green");
                    } else
                        parentSpan.css("border","1px solid red");
                }
            );
        });
    });
</script>

<div class="workingArea">
    <ol class="breadcrumb">
        <li><a href="admin_category_list">所有分类</a></li>
        <li><a href="admin_product_list?cid=${p.category.id}">${p.category.name}</a></li>
        <li class="active">${p.name}</li>
        <li class="active">编辑产品属性</li>
    </ol>

    <div class="editPVDiv">
        <c:forEach items="${pvs}" var="pv">
            <div class="eachPV">
                <span class="pvName">${pv.property.name}</span>
                <span class="pvValue"><input type="text" pvid="${pv.id}" value="${pv.value}" class="pvValue"/></span>
            </div>
        </c:forEach>
        <div style="clear: both;"></div>
    </div>
</div>

</body>
</html>
