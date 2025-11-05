import React from 'react';

const LikeButton = ({ onLike }) => {
  return (
    <button
      onClick={onLike}
      className="px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-700"
    >
      Like
    </button>
  );
};

export default LikeButton;
