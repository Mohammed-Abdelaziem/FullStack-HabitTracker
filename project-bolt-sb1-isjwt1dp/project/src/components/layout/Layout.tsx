import React from 'react';
import { Navbar } from './Navbar';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      <main className="lg:pl-64 flex flex-col min-h-screen">
        <div className="flex-1 px-4 py-8 lg:px-8">
          {children}
        </div>
      </main>
    </div>
  );
};