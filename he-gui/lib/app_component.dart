import 'dart:async';

import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
import 'package:angular2_components/angular2_components.dart';

import 'jobs/jobs_component.dart';
import 'service/jobs_service.dart';
import 'jobs/job_detail_component.dart';
import 'devop/job_devops_comp.dart';
import 'jobs/add_job_component.dart';
import 'jobs/recipes_component.dart';
import 'jobs/recipe_detail_component.dart';
import 'jobs/statistics_component.dart';

@Component(
    selector: 'my-app',
    templateUrl: 'app_component.html',
    styleUrls: const ['app_component.css'],
    directives: const [ROUTER_DIRECTIVES, materialDirectives],
    providers: const [JobsService, ROUTER_PROVIDERS, materialProviders])
@RouteConfig(const [
//  const Route(path: '/dashboard',  name: 'Dashboard', component: DashboardComponent),
  const Route(path: '/jobs_detail/:id', name: 'JobDetail', component: JobDetailComponent),
  const Route(path: '/jobs', name: 'Jobs', component: JobsComponent),
  const Route(path: '/job_devops', name: 'JobDevOps', component: JobDevOpsComponent),
  const Route(path: '/add_job', name: 'AddJob', component: AddJobComponent),
  const Route(path: '/statistics', name: 'Statistics', component: StatisticsComponent),
  const Route(path: '/recipes', name: 'Recipes', component: RecipesComponent),
  const Route(path: '/add_recipe', name: 'AddRecipe', component: RecipeDetailComponent),
  const Route(path: '/recipe_detail/:id', name: 'RecipeDetail', component: RecipeDetailComponent)
])
class AppComponent {
  String title = 'Tour of Heroes';

  final Router _router;

  AppComponent(this._router);

  void showRecipes() {
    gotoDetail('Recipes');
  }

  void showJobs() {
    gotoDetail('Jobs');
  }

  Future<Null> gotoDetail(name) => _router.navigate([
        name,
      ]);
}
