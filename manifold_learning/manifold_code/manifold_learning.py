#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 12:44:48 2021

@author: amparomunoz
"""
from sklearn import manifold
from functools import partial
from sklearn.manifold import Isomap

class  MANIFOLD_LEARNING:  
    def __init__(self):
          print("MANIFOLD_LEARNING")
          
    def umap(self,parameters):
        from umap import UMAP
        model = UMAP(n_components=parameters["n_components"],n_neighbors=parameters["n_neighbors"], metric='euclidean',init='random', random_state=0)
        return model
        
    def tsne(self,parameters):
        model=manifold.TSNE(n_components=parameters["n_components"], init='pca',random_state=0)
        return model
        
    def lle(self,parameters):
        LLE = partial(manifold.LocallyLinearEmbedding,
                n_components=parameters["n_components"],n_neighbors=parameters["n_neighbors"],eigen_solver='auto')
        model= LLE(method='standard')
        return model
    
    def isomap(self,parameters):
        n_neighbors = parameters['n_neighbors']
        n_components=parameters['n_components']
        model =  Isomap(n_components=2)
        return model
      
  
