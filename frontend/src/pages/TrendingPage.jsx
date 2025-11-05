import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getTrending } from '../features/trending/trendingSlice';
import RecipeCard from '../components/RecipeCard';

const TrendingPage = () => {
  const dispatch = useDispatch();
  const { trending, isLoading } = useSelector((state) => state.trending);

  useEffect(() => {
    dispatch(getTrending());
  }, [dispatch]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container px-4 py-8 mx-auto">
      <h1 className="mb-8 text-4xl font-bold">Trending Recipes</h1>
      <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-3">
        {trending.map((recipe) => (
          <RecipeCard key={recipe.id} recipe={recipe} />
        ))}
      </div>
    </div>
  );
};

export default TrendingPage;
