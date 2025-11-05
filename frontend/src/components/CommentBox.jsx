import React, { useState } from 'react';

const CommentBox = ({ onComment }) => {
  const [comment, setComment] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onComment(comment);
    setComment('');
  };

  return (
    <form onSubmit={handleSubmit} className="mt-4">
      <textarea
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
        rows="3"
        placeholder="Add a comment..."
      ></textarea>
      <button
        type="submit"
        className="px-4 py-2 mt-2 text-white bg-indigo-600 rounded-md hover:bg-indigo-700"
      >
        Submit
      </button>
    </form>
  );
};

export default CommentBox;
