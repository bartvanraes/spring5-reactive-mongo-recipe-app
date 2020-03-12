package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm in the service");

        return recipeReactiveRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {

        return recipeReactiveRepository.findById(id)
                .single();

        /*if (!recipeOptional.isPresent()) {
            throw new NotFoundException("Recipe Not Found. For ID value: " + id );
        }

        return recipeOptional.get();*/
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> findCommandById(String id) {

        return findById(id)
                .map(recipeToRecipeCommand::convert)
                .doOnEach(recipeCommand -> {
                    List<IngredientCommand> ingredientCommands = recipeCommand.get().getIngredients();
                    if (ingredientCommands != null && ingredientCommands.size() > 0) {
                        ingredientCommands.forEach(ingredientCommand -> ingredientCommand.setRecipeId(id));
                    }
                });

                /*.filter(ingredients -> ingredients != null && ingredients.size() > 0)
                .doOnEach(ingredientCommands -> {
                   // if (ingredientCommands.hasValue() && ingredientCommands.get() != null && ingredientCommands.get().size() > 0)
                    ingredientCommands.get().forEach(ic -> {
                        ic.setRecipeId(id);
                    });
                })
                .map(ingredientCommands -> ingredientCommands.get(0).get)*/
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        return recipeReactiveRepository.save(detachedRecipe)
                .doOnEach(recipe -> log.debug("Saved RecipeId: " + recipe.get().getId()))
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public void deleteById(String idToDelete) {
        recipeReactiveRepository.deleteById(idToDelete).block();
    }
}
