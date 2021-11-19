#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 10:55:19 2021

@author: amparomunoz
"""
import plotly.express as px
from plotly.offline import plot as plot_ly
class  MANIFOLD_UTILITIES:  
  def __init__(self):
      print("MANIFOLD_UTILITIES")
      
  def plot2d_scatter_interactive(self,projections,targets,title):   
        fig_2d = px.scatter(projections, x=0, y=1, 
                            color=targets,
                            symbol=targets, 
                            labels={'color': ''},
                            width=1200, 
                            height=800,
                            title=title)
        fig_2d.show()

def plot3d_scatter_interactive(self,projections,targets,title):         
    fig = px.scatter_3d(
        projections, x=0, y=1, z=2,
        color=targets, labels={'color': 'species'}
    )
    fig.update_traces(marker_size=8)
    fig.show()
     

 
  
 