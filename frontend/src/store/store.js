import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import recipeReducer from '../features/recipes/recipeSlice';
import interactionReducer from '../features/interactions/interactionSlice';
import userReducer from '../features/users/userSlice';
import trendingReducer from '../features/trending/trendingSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    recipes: recipeReducer,
    interactions: interactionReducer,
    users: userReducer,
    trending: trendingReducer,
  },
});
