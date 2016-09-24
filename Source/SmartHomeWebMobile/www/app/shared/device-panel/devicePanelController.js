app.directive("devicePanel", function() {
    return {
        restrict: "E",
        scope: {
            device: "=",
            typeimg: "@",
            mainactionname: "@"
        },
        templateUrl: "app/shared/device-panel/devicePanelView.html",
        controllerAs: "devicePanelCtrl",
        controller: function($scope) {
            var self = this;

            self.init = function() {
                $(".panel-collapse").on('hide.bs.collapse', function () {
                    var deviceId = $(this).attr("id").slice(-1);
                    $("#devicePanelToggleIcon" + deviceId).removeClass("glyphicon-menu-down");
                    $("#devicePanelToggleIcon" + deviceId).addClass("glyphicon-menu-right");

                });
                $(".panel-collapse").on('show.bs.collapse', function () {
                    var deviceId = $(this).attr("id").slice(-1);
                    $("#devicePanelToggleIcon" + deviceId).removeClass("glyphicon-menu-right");
                    $("#devicePanelToggleIcon" + deviceId).addClass("glyphicon-menu-down");

                });
            }

        }
    }
})


