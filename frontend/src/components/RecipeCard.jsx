import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

const RecipeCard = ({ recipe }) => {
  return (
    <Link to={`/recipes/${recipe.id}`}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="overflow-hidden bg-white rounded-lg shadow-md dark:bg-gray-800"
      >
        <img
          src={recipe.imageUrl || 'https://via.placeholder.com/300'}
          alt={recipe.name}
          className="object-cover w-full h-48"
        />
        <div className="p-6">
          <h3 className="text-xl font-bold">{recipe.name}</h3>
          <p className="mt-2 text-gray-600 dark:text-gray-400">{recipe.description}</p>
        </div>
      </motion.div>
    </Link>
  );
};

export default RecipeCard;
