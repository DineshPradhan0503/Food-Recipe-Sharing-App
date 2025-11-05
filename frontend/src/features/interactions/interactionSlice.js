import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../../api/axiosInstance';
import { toast } from 'react-hot-toast';

const initialState = {
  interactions: [],
  isLoading: false,
};

export const getInteractions = createAsyncThunk(
  'interactions/getInteractions',
  async (recipeId, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.get(`/interactions/recipe/${recipeId}`);
      return response.data;
    } catch (error) {
      toast.error(error.response.data.message || 'Failed to fetch interactions');
      return rejectWithValue(error.response.data);
    }
  }
);

export const likeRecipe = createAsyncThunk('interactions/likeRecipe', async (interactionData, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.post('/interactions', { ...interactionData, type: 'LIKE' });
    toast.success('Recipe liked!');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to like recipe');
    return rejectWithValue(error.response.data);
  }
});

export const commentOnRecipe = createAsyncThunk(
  'interactions/commentOnRecipe',
  async (interactionData, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.post('/interactions', { ...interactionData, type: 'COMMENT' });
      toast.success('Comment added!');
      return response.data;
    } catch (error) {
      toast.error(error.response.data.message || 'Failed to add comment');
      return rejectWithValue(error.response.data);
    }
  }
);

const interactionSlice = createSlice({
  name: 'interactions',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getInteractions.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getInteractions.fulfilled, (state, action) => {
        state.isLoading = false;
        state.interactions = action.payload;
      })
      .addCase(getInteractions.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(likeRecipe.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(likeRecipe.fulfilled, (state, action) => {
        state.isLoading = false;
        state.interactions.push(action.payload);
      })
      .addCase(likeRecipe.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(commentOnRecipe.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(commentOnRecipe.fulfilled, (state, action) => {
        state.isLoading = false;
        state.interactions.push(action.payload);
      })
      .addCase(commentOnRecipe.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

export default interactionSlice.reducer;
