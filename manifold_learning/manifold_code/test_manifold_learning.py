#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 12:44:48 2021

@author: amparomunoz
"""
import sys
sys.path.append('../')

from manifold_learning import MANIFOLD_LEARNING


Manifold_learning= MANIFOLD_LEARNING() 

parameters={
'isomap':{
    'n_components':2,
    'n_neighbors':10
  } ,
'lle':{
    'n_components':2,
    'n_neighbors':10
  }  ,
'tsne':{
    'n_components':2,
    'n_neighbors':10
  } 
}

       
        
model1 =Manifold_learning.tsne(parameters["tsne"])
model2 =Manifold_learning.lle(parameters["lle"])

#model2 =Manifold_learning.isomap(parameters["isomap"])
        

  