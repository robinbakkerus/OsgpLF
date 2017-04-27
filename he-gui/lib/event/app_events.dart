import '../model/recipe.dart';
import '../model/job.dart';

class AddRecipeEvent {
  final Recipe recipe;
  AddRecipeEvent(this.recipe);
}

class AddJobEvent {
  final Job job;
  AddJobEvent(this.job);
}
