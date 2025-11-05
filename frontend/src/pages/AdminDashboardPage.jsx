import React from 'react';

const AdminDashboardPage = () => {
  return (
    <div className="container px-4 py-8 mx-auto">
      <h1 className="mb-8 text-4xl font-bold">Admin Dashboard</h1>
      <div className="p-8 bg-white rounded-lg shadow-md dark:bg-gray-800">
        <h2 className="text-2xl font-bold">Moderation Tools</h2>
        <p className="mt-4 text-gray-600 dark:text-gray-400">
          Recipe and comment moderation tools will be implemented here.
        </p>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
