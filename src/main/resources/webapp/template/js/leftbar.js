/**
 * Created by lipingfei on 2017/10/16.
 */
 var tableOperates = {
     //获取异常信息
     getErrorMessage: function (data) {
         switch (data) {
             case -40001:
                 return "参数不合法";
             case -40402:
                 return "暂无数据";
             case -40000:
                 return "请求异常，请联系管理员";
             case -40100:
                 return "认证失败";
             default:
                 return "请求异常，请稍后重试";
         }
         return data;
     }
 };
$(function () {
    //请求左侧导航展示栏
    $.ajax({
        type: "GET",
        url: "/backend/sys/get_user_module_list",
        cache: false,
        dataType: "json",
        success: function (result) {
            if ((result.hasOwnProperty('status') && result.status != 0)
                || (result.hasOwnProperty('statusCodeValue') && result.statusCodeValue != 200)) {
                toastr.error(tableOperates.getErrorMessage(result.status));
                return;
            }
            var returnData = {};
            returnData.data = result.data;//返回的数据列表

            //绑定左侧导航栏
            var leftHtml="";
            var path=$("#contentPath").val();
            if(result.data.length>0) {
                $.each(result.data, function (i, v) {
                    if(v.parentId==0) {
                        leftHtml += "<li class=\"treeview\">";
                        leftHtml += "<a href=\"#\">";
                        leftHtml += "<i class=\"fa fa-edit\"></i> <span>" + v.moduleName + "</span>";
                        leftHtml += "<span class=\"pull-right-container\">";
                        leftHtml += "<i class=\"fa fa-angle-left pull-right\"></i>";
                        leftHtml += "</span></a>";
                        leftHtml += " <ul class=\"treeview-menu\">";
                        $.each(result.data, function (x, y) {
                            if (y.parentId == v.moduleId) {
                                var  herf=path+y.path;
                                leftHtml += "<li><a href=\""+herf+"\"><i class=\"fa fa-circle-o\"></i>" + y.moduleName + "</a></li>"
                            }
                        });
                        leftHtml += "</ul></li>";
                    }
                });
            }
            $(".sidebar-menu").append(leftHtml);
        },

        error: function (result) {
            return;
        }
    });
    
});
