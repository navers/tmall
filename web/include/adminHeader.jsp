<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>

<head>
    <script src="js/jquery/2.0.0/jquery.min.js"></script>
    <link href="css/bootstrap/3.3.6/bootstrap.min.css" rel="stylesheet">
    <script src="js/bootstrap/3.3.6/bootstrap.min.js"></script>
    <link href="css/back/style.css" rel="stylesheet">

    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Cache-Control" content="must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>

    <script>
        function checkEmpty(id,name) {
            var value = $("#"+id).val()
            if (value.length==0){
                alert(name+"不能为空");
                $("#"+id)[0].focus();
                return false;
            }
            return true;
        }

        function checkNumber(id,name) {
            var value = $("#"+id).val()
            if (value.length==0){
                alert(name+"不能为空");
                $("#"+id)[0].focus();
                return false;
            }
            if (isNaN(value)){
                alert(name+"必须是数字");
                $("#"+id)[0].focus();
                return false;
            }
            return true;
        }
        function checkInt(id,name) {
            var value = $("#"+id).val()
            if (value.length==0){
                alert(name+"不能为空");
                $("#"+id)[0].focus();
                return false;
            }
            if (parseInt(value)!=value){
                alert(name+"必须是整数");
                $("#"+id)[0].focus();
                return false;
            }
            return true;
        }

        $(function () {
            $("a").click(function () {
                var deleteLink = $(this).attr("deleteLink");
                if ("true"==deleteLink){
                    var confirmDelete = confirm("确认要删除吗?");
                    if (confirmDelete)
                        return true;
                    return false;
                }
            });
        });
    </script>
</head>

<body>