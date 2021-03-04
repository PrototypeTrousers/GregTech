package gregtech.integration.jei.recipe;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.Recipe.ChanceEntry;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.unification.OreDictUnifier;
import gregtech.integration.jei.utils.JEIHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class GTRecipeWrapper implements IRecipeWrapper {

    private static final int lineHeight = 10;
    private final RecipeMap<?> recipeMap;
    private final Recipe recipe;
    private final Map<Object,ChanceEntry> chanceEntryMap = new HashMap<>(1,1f);
    private final Map<Object,Boolean> notConsumedMap = new HashMap<>(1,1f);

    public GTRecipeWrapper(RecipeMap<?> recipeMap, Recipe recipe) {
        this.recipeMap = recipeMap;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (!recipe.getInputs().isEmpty()) {
            List<CountableIngredient> recipeInputs = recipe.getInputs();
            List<List<ItemStack>> matchingInputs = new ArrayList<>(recipeInputs.size());
            for (CountableIngredient ingredient : recipeInputs) {
                List<ItemStack> ingredientValues = Arrays.stream(ingredient.getIngredient().getMatchingStacks())
                    .map(ItemStack::copy)
                    .sorted(OreDictUnifier.getItemStackComparator())
                    .collect(Collectors.toList());
                ingredientValues.forEach(stack -> {
                    if (ingredient.getCount() == 0) {
                        notConsumedMap.put(stack,true);
                        stack.setCount(1);
                    } else stack.setCount(ingredient.getCount());
                });
                matchingInputs.add(ingredientValues);
            }
            ingredients.setInputLists(VanillaTypes.ITEM, matchingInputs);
        }

        if (!recipe.getFluidInputs().isEmpty()) {
            List<FluidStack> recipeInputs = recipe.getFluidInputs()
                .stream().map(FluidStack::copy)
                .collect(Collectors.toList());
            recipeInputs.forEach(stack -> {
                if (stack.amount == 0) {
                    notConsumedMap.put(stack,true);
                    stack.amount = 1;
                }
            });
            ingredients.setInputs(VanillaTypes.FLUID, recipeInputs);
        }

        if (!recipe.getOutputs().isEmpty() || !recipe.getChancedOutputs().isEmpty()) {
            List<ItemStack> recipeOutputs = recipe.getOutputs()
                .stream().map(ItemStack::copy).collect(Collectors.toList());
            List<ItemStack> chancedOutputs = new ArrayList<>();
            for (ChanceEntry chancedEntry : recipe.getChancedOutputs()) {
                ItemStack chancedStack = chancedEntry.getItemStack();
                chanceEntryMap.put(chancedStack,chancedEntry);
                chancedOutputs.add(chancedStack);
            }
            chancedOutputs.sort(Comparator.comparing(o -> chanceEntryMap.get(o).getChance()).reversed());
            recipeOutputs.addAll(chancedOutputs);
            ingredients.setOutputs(VanillaTypes.ITEM, recipeOutputs);
        }

        if (!recipe.getFluidOutputs().isEmpty()) {
            List<FluidStack> recipeOutputs = recipe.getFluidOutputs()
                .stream().map(FluidStack::copy).collect(Collectors.toList());
            ingredients.setOutputs(VanillaTypes.FLUID, recipeOutputs);
        }
    }

    public void addTooltip(int slotIndex, boolean input, Object ingredient, List<String> tooltip) {
        if (ingredient instanceof ItemStack || ingredient instanceof FluidStack) {
            if (chanceEntryMap.containsKey(ingredient)) {
                String chanceString = Recipe.formatChanceValue(chanceEntryMap.get(ingredient).getChance());
                String boostString = Recipe.formatChanceValue(chanceEntryMap.get(ingredient).getBoostPerTier());
                tooltip.add(I18n.format("gregtech.recipe.chance", chanceString, boostString));
            } else if (notConsumedMap.containsKey(ingredient)) {
                tooltip.add(I18n.format("gregtech.recipe.not_consumed"));
            }
        } else {
            throw new IllegalArgumentException("Unknown ingredient type: " + ingredient.getClass());
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int yPosition = recipeHeight - getPropertyListHeight();
        minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.total", Math.abs((long) recipe.getEUt()) * recipe.getDuration()), 0, yPosition, 0x111111);
        minecraft.fontRenderer.drawString(I18n.format(recipe.getEUt() >= 0 ? "gregtech.recipe.eu" : "gregtech.recipe.eu_inverted", Math.abs(recipe.getEUt()), JEIHelpers.getMinTierForVoltage(recipe.getEUt())), 0, yPosition += lineHeight, 0x111111);
        minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.duration", recipe.getDuration() / 20f), 0, yPosition += lineHeight, 0x111111);
        for (String propertyKey : recipe.getPropertyKeys()) {
            minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe." + propertyKey,
                recipe.<Object>getProperty(propertyKey)), 0, yPosition += lineHeight, 0x111111);
        }
    }

    private int getPropertyListHeight() {
        return (recipe.getPropertyKeys().size() + 3) * lineHeight;
    }

}
