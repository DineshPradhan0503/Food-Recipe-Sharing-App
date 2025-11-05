import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { logout } from '../features/auth/authSlice';
import { useTheme } from '../hooks/useTheme';

const Navbar = () => {
  const dispatch = useDispatch();
  const { isAuthenticated, user } = useSelector((state) => state.auth);
  const [theme, toggleTheme] = useTheme();

  const handleLogout = () => {
    dispatch(logout());
  };

  return (
    <nav className="bg-white shadow-md dark:bg-gray-800">
      <div className="container px-4 py-4 mx-auto">
        <div className="flex items-center justify-between">
          <Link to="/" className="text-2xl font-bold text-indigo-600">
            RecipeApp
          </Link>
          <div className="flex items-center space-x-4">
            <Link to="/recipes" className="text-gray-600 dark:text-gray-300 hover:text-indigo-600">
              Recipes
            </Link>
            <Link to="/trending" className="text-gray-600 dark:text-gray-300 hover:text-indigo-600">
              Trending
            </Link>
            {isAuthenticated ? (
              <>
                {user.roles.includes('ADMIN') && (
                  <Link
                    to="/admin"
                    className="text-gray-600 dark:text-gray-300 hover:text-indigo-600"
                  >
                    Admin
                  </Link>
                )}
                <Link
                  to="/profile"
                  className="text-gray-600 dark:text-gray-300 hover:text-indigo-600"
                >
                  Profile
                </Link>
                <button
                  onClick={handleLogout}
                  className="px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-700"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="text-gray-600 dark:text-gray-300 hover:text-indigo-600"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
                >
                  Register
                </Link>
              </>
            )}
            <button onClick={toggleTheme} className="p-2 rounded-md focus:outline-none focus:ring">
              {theme === 'light' ? '🌙' : '☀️'}
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
