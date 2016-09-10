app.directive("devicePanel", function() {
    return {
        restrict: "E",
        scope: {
            name: "@",
            typeimg: "@"
        },
        templateUrl: "app/shared/device-panel/devicePanelView.html"
    }
})
