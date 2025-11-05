import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../../api/axiosInstance';
import { toast } from 'react-hot-toast';

const initialState = {
  recipes: [],
  recipe: null,
  isLoading: false,
};

export const getRecipes = createAsyncThunk('recipes/getRecipes', async (_, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.get('/recipes');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to fetch recipes');
    return rejectWithValue(error.response.data);
  }
});

export const getRecipeById = createAsyncThunk('recipes/getRecipeById', async (id, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.get(`/recipes/${id}`);
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to fetch recipe');
    return rejectWithValue(error.response.data);
  }
});

export const createRecipe = createAsyncThunk('recipes/createRecipe', async (recipeData, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.post('/recipes', recipeData);
    toast.success('Recipe created successfully!');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to create recipe');
    return rejectWithValue(error.response.data);
  }
});

const recipeSlice = createSlice({
  name: 'recipes',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getRecipes.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getRecipes.fulfilled, (state, action) => {
        state.isLoading = false;
        state.recipes = action.payload;
      })
      .addCase(getRecipes.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(getRecipeById.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getRecipeById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.recipe = action.payload;
      })
      .addCase(getRecipeById.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(createRecipe.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(createRecipe.fulfilled, (state, action) => {
        state.isLoading = false;
        state.recipes.push(action.payload);
      })
      .addCase(createRecipe.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

export default recipeSlice.reducer;
