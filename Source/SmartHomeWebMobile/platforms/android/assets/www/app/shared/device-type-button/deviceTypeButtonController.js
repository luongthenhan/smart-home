app.directive("deviceTypeButton", function($location) {
    return {
        restrict: "E",
        scope: {
            typename: "@",
            typeimg: "@"
        },
        templateUrl: "app/shared/device-type-button/deviceTypeButtonView.html",
        controllerAs: 'deviceTypeCtrl',
        controller: function() {
            var self = this;
            this.toDeviceList = function() {
                $location.path("/device_list");
            }
        }
    }
})
