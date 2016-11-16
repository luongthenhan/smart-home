app.directive("homePanel", ['MainService', function(MainService) {
    return {
        restrict: "E",
        scope: {
            home : "="
        },
        templateUrl: "app/shared/home-panel/homePanelView.html",
        controllerAs: 'homePanelCtrl',
        controller: function($scope, $route, $location) {
            var self = this;

            self.home = $scope.home;

            self.toHome = function() {
                MainService.selectedHome = $scope.home;
                MainService.getHome(self);
                $location.path("/home");
            }

            self.deleteHome = function() {
                MainService.deleteHome($scope.home);
                $route.reload();
            }

            self.toggleEnableDisableHome = function() {
                if ($scope.home.enabled == false) {
                    self.enableHome();
                } else {
                    self.showDisableHomeModal();
                }
            }

            self.disableHome = function() {
                MainService.disableHome($scope.home);
            }

            self.enableHome = function() {
                MainService.enableHome($scope.home);
            }

            // for UI handling
            self.showDeleteHomeModal = function() {
                $("#home-panel-delete-home-modal" + $scope.home.id).modal('show');
            }

            self.showDisableHomeModal = function() {
                $("#home-panel-disable-home-modal" + $scope.home.id).modal('show');
            }
        }
    };
}]);