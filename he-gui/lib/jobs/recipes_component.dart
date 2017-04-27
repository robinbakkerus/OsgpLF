import 'dart:async';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
//import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/recipe.dart';
import 'job_detail_component.dart';
import '../service/jobs_service.dart';
import '../pipe/app_pipes.dart';
import '../event/app_events.dart';
import '../app_constants.dart';

@Component(
    selector: 'my-recipes',
    templateUrl: 'recipes_component.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime],
    directives: const [materialDirectives, JobDetailComponent],
    providers: const [materialProviders]
)

class RecipesComponent implements OnInit {
  List<Recipe> recipes;
  Recipe selectedRecipe;

  final JobsService _jobsService;
  final Router _router;

  RecipesComponent(this._jobsService, this._router);

  Future<Null> getRecipes() async {
    recipes = await _jobsService.getRecipes();
  }

  void ngOnInit() {
    getRecipes();

    AppConstants.eventBus.on(AddRecipeEvent).listen((AddRecipeEvent event) {
      recipes.add(event.recipe);
    });
  }

  void onSelect(Recipe recipe) {
    selectedRecipe = recipe;
    gotoDetail();
  }

  void gotoDetail() {
    _router.navigate([
      'RecipeDetail',
      {'id': selectedRecipe.id.toString()}
    ]);
  }

  void addRecipe() {
    _router.navigate([
      'AddRecipe'
    ]);
  }

}
