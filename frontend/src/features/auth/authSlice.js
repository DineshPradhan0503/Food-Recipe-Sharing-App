import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axiosInstance from '../../api/axiosInstance';
import { toast } from 'react-hot-toast';
import jwt_decode from 'jwt-decode';

const initialState = {
  user: null,
  token: localStorage.getItem('token') || null,
  isAuthenticated: false,
  isLoading: false,
};

export const register = createAsyncThunk('auth/register', async (userData, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.post('/auth/register', userData);
    toast.success('Registration successful!');
    return response.data;
  } catch (error) {
    toast.error(error.response.data.message || 'Registration failed');
    return rejectWithValue(error.response.data);
  }
});

export const login = createAsyncThunk('auth/login', async (userData, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.post('/auth/login', userData);
    const { token } = response.data.data;
    localStorage.setItem('token', token);
    const decoded = jwt_decode(token);
    toast.success('Login successful!');
    return { token, user: decoded };
  } catch (error) {
    toast.error(error.response.data.message || 'Login failed');
    return rejectWithValue(error.response.data);
  }
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      localStorage.removeItem('token');
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      toast.success('Logged out successfully!');
    },
    loadUser: (state) => {
      const token = localStorage.getItem('token');
      if (token) {
        const decoded = jwt_decode(token);
        state.user = decoded;
        state.token = token;
        state.isAuthenticated = true;
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(register.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(register.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(register.rejected, (state) => {
        state.isLoading = false;
      })
      .addCase(login.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isAuthenticated = true;
        state.token = action.payload.token;
        state.user = action.payload.user;
      })
      .addCase(login.rejected, (state) => {
        state.isLoading = false;
      });
  },
});

export const { logout, loadUser } = authSlice.actions;

export default authSlice.reducer;
