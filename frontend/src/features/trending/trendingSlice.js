import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../../api/axiosInstance';
import { toast } from 'react-hot-toast';

const initialState = {
  trending: [],
  isLoading: false,
};

export const getTrending = createAsyncThunk('trending/getTrending', async (_, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.get('/trending');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to fetch trending recipes');
    return rejectWithValue(error.response.data);
  }
});

const trendingSlice = createSlice({
  name: 'trending',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getTrending.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getTrending.fulfilled, (state, action) => {
        state.isLoading = false;
        state.trending = action.payload;
      })
      .addCase(getTrending.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

export default trendingSlice.reducer;
