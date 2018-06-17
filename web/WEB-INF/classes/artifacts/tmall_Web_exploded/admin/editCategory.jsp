<%@ page contentType="text/html; charset=UTF-8" language="java"
         pageEncoding="UTF-8" import="java.util.*" %>
<html>
<head>
    
</head>
<body>
    <%@ include file="../include/adminHeader.jsp"%>
    <%@ include file="../include/adminNavigator.jsp"%>
    <title>编辑分类</title>

    <script>
        $(function(){

            $("#editForm").submit(function(){
                if(!checkEmpty("name","分类名称"))
                    return false;

                return true;
            });
        });

    </script>

    <div class="workingArea">
        <ol class="breadcrumb">
            <li><a href="admin_category_list">所有分类</a> </li>
            <li class="active"></li>
        </ol>

        <div class="panel panel-warning editDiv">
            <div class="panel-heading">编辑分类</div>
            <div class="panel-body">
                <form method="post" id="editForm" action="admin_category_update" enctype="multipart/form-data">
                    <table class="editTable">
                        <tr>
                            <td>分类名称</td>
                            <td><input name="name" id="name" value="${c.name}" type="text" class="form-control"></td>
                        </tr>
                        <tr>
                            <td>分类图片</td>
                            <td><input id="categoryPic" accept="image/*" type="file" name="filepath"></td>
                        </tr>
                        <tr>
                            <td colspan="2" align="center">
                                <input type="hidden" name="id" value="${c.id}">
                                <button type="submit" class="btn btn-success">提 交</button>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
