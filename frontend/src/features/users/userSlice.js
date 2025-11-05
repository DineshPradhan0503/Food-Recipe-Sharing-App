import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../../api/axiosInstance';
import { toast } from 'react-hot-toast';

const initialState = {
  profile: null,
  isLoading: false,
};

export const getProfile = createAsyncThunk('users/getProfile', async (_, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.get('/users/profile');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to fetch profile');
    return rejectWithValue(error.response.data);
  }
});

export const updateProfile = createAsyncThunk('users/updateProfile', async (profileData, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.put('/users/profile', profileData);
    toast.success('Profile updated successfully!');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Failed to update profile');
    return rejectWithValue(error.response.data);
  }
});

const userSlice = createSlice({
  name: 'users',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getProfile.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getProfile.fulfilled, (state, action) => {
        state.isLoading = false;
        state.profile = action.payload;
      })
      .addCase(getProfile.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(updateProfile.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(updateProfile.fulfilled, (state, action) => {
        state.isLoading = false;
        state.profile = action.payload;
      })
      .addCase(updateProfile.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

export default userSlice.reducer;
