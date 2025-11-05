import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getRecipeById } from '../features/recipes/recipeSlice';
import { getInteractions, likeRecipe, commentOnRecipe } from '../features/interactions/interactionSlice';
import { useParams } from 'react-router-dom';
import CommentBox from '../components/CommentBox';
import LikeButton from '../components/LikeButton';

const RecipeDetailPage = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { recipe, isLoading: recipeLoading } = useSelector((state) => state.recipes);
  const { interactions, isLoading: interactionsLoading } = useSelector((state) => state.interactions);
  const { user } = useSelector((state) => state.auth);

  useEffect(() => {
    dispatch(getRecipeById(id));
    dispatch(getInteractions(id));
  }, [dispatch, id]);

  const handleLike = () => {
    dispatch(likeRecipe({ recipeId: id, userId: user.id }));
  };

  const handleComment = (comment) => {
    dispatch(commentOnRecipe({ recipeId: id, userId: user.id, comment }));
  };

  if (recipeLoading || interactionsLoading) {
    return <div>Loading...</div>;
  }

  if (!recipe) {
    return <div>Recipe not found</div>;
  }

  return (
    <div className="container px-4 py-8 mx-auto">
      <div className="max-w-4xl mx-auto">
        <img
          src={recipe.imageUrl || 'https://via.placeholder.com/800x400'}
          alt={recipe.name}
          className="object-cover w-full h-96 rounded-lg shadow-md"
        />
        <div className="mt-8">
          <h1 className="text-4xl font-bold">{recipe.name}</h1>
          <p className="mt-4 text-gray-600 dark:text-gray-400">{recipe.description}</p>
        </div>
        <div className="mt-8">
          <LikeButton onLike={handleLike} />
        </div>
        <div className="mt-8">
          <h2 className="text-2xl font-bold">Comments</h2>
          <CommentBox onComment={handleComment} />
          <div className="mt-4 space-y-4">
            {interactions
              .filter((interaction) => interaction.type === 'COMMENT')
              .map((interaction) => (
                <div key={interaction.id} className="p-4 bg-white rounded-lg shadow-md dark:bg-gray-800">
                  <p>{interaction.comment}</p>
                </div>
              ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecipeDetailPage;
