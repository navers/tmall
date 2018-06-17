<%@ page contentType="text/html; charset=UTF-8" language="java"
         pageEncoding="UTF-8" import="java.util.*" %>

<%@ include file="../include/adminHeader.jsp"%>
<%@ include file="../include/adminNavigator.jsp"%>

<script>
    $(function () {
        $(".addFormSingle").submit(function () {
            if (checkEmpty("filepathSingle","图片文件")) {
                $("#filepathSingle").value("");
                return true;
            }
            return false;
        });

        $(".addFormDetail").submit(function () {
            if (checkEmpty("filepathDetail","图片文件")) {
                //$("#filepathDetail").value("");
                return true;
            }
            return false;
        });


    });
</script>

<title>产品图片管理</title>

<div class="workingArea">
    <ol class="breadcrumb">
        <li><a href="admin_category_list">所有分类</a></li>
        <li><a href="admin_product_list?cid=${p.category.id}">${p.category.name}</a></li>
        <li class="active">${p.name}</li>
        <li class="active">产品图片管理</li>
    </ol>

    <table class="addPictureTable" align="center">
        <tr>
            <td class="addPictureTableTD">
                <div class="panel panel-warning addPictureDiv">
                    <div class="panel-heading">新增产品<b class="text-primary">单个</b>图片</div>
                    <div class="panel-body">
                        <form method="post" class="addFormSingle" action="admin_productImage_add" enctype="multipart/form-data">
                            <table class="addTable">
                                <tr>
                                    <td>请选择本地图片 尺寸图400×400 最佳</td>
                                </tr>
                                <tr><td><input id="filepathSingle" type="file" name="filepath" /></td> </tr>
                                <tr class="submitTR">
                                   <td align="center">
                                       <input type="hidden" name="type" value="type_single"/>
                                       <input type="hidden" name="pid" value="${p.id}"/>
                                       <button type="submit" class="btn btn-success">提交</button>
                                   </td>
                                </tr>
                            </table>
                        </form>
                    </div>
                </div>

                <table class="table table-striped table-bordered table-hover  table-condensed">
                    <thead>
                        <tr class="success">
                            <td>ID</td>
                            <td>产品单个图片缩略图</td>
                            <td>删除</td>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${pisSingle}" var="pi">
                            <tr>
                                <td>${pi.id}</td>
                                <td>
                                    <a title="点击查看原图" href="img/productSingle/${pi.id}.jsp">
                                        <img height="50px" src="img/productSingle/${pi.id}.jsp"/>
                                    </a>
                                </td>
                                <td>
                                    <a href="admin_productImage_delete?id=${pi.id}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </td>

            <td class="addPictureTableTD">
                <div class="panel panel-warning addPictureDiv">
                    <div class="panel-heading">新增产品<b class="text-primary">详情</b>图片</div>
                    <div class="panel-body">
                        <form method="post" class="addFormDetail" action="admin_productImage_add" enctype="multipart/form-data">
                            <table class="addTable">
                                <tr>
                                    <td>请选择本地图片 宽度790 最佳</td>
                                </tr>
                                <tr><td><input id="filepathDetail" type="file" name="filepath" /></td> </tr>
                                <tr class="submitTR">
                                   <td align="center">
                                       <input type="hidden" name="type" value="type_detail"/>
                                       <input type="hidden" name="pid" value="${p.id}"/>
                                       <button type="submit" class="btn btn-success">提交</button>
                                   </td>
                                </tr>
                            </table>
                        </form>
                    </div>
                </div>

                <table class="table table-striped table-bordered table-hover  table-condensed">
                    <thead>
                        <tr class="success">
                            <td>ID</td>
                            <td>产品详情图片缩略图</td>
                            <td>删除</td>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${pisDetail}" var="pi">
                            <tr>
                                <td>${pi.id}</td>
                                <td>
                                    <a title="点击查看原图" href="img/productDetail/${pi.id}.jsp">
                                        <img height="50px" src="img/productDetail/${pi.id}.jsp"/>
                                    </a>
                                </td>
                                <td>
                                    <a href="admin_productImage_delete?id=${pi.id}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </td>
        </tr>
    </table>
</div>

<%@include file="../include/adminFooter.jsp"%>