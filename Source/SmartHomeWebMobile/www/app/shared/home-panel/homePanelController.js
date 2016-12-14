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
            self.beforeUpdateHome = null;

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

            self.updateHome = function() {
                MainService.updateHome($scope.home);
            }

            self.disableHome = function() {
                MainService.disableHome($scope.home);
            }

            self.enableHome = function() {
                MainService.enableHome($scope.home);
            }

            self.resetHomeValue = function() {
                $scope.home.name = self.beforeUpdateHome.name;
                $scope.home.address = self.beforeUpdateHome.address;
                $scope.home.description = self.beforeUpdateHome.description;
            }

            // for UI handling
            self.showEditHomeModal = function() {
                self.beforeUpdateHome = angular.copy($scope.home);
                $("#home-panel-edit-home-modal" + $scope.home.id).modal('show');
            }

            self.showDeleteHomeModal = function() {
                $("#home-panel-delete-home-modal" + $scope.home.id).modal('show');
            }

            self.showDisableHomeModal = function() {
                $("#home-panel-disable-home-modal" + $scope.home.id).modal('show');
            }
        }
    };
}]);