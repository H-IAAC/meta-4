#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 10:55:19 2021

@author: amparomunoz
"""
import plotly.express as px
from plotly.offline import plot as plot_ly
class  RAW_PLOTS:  
  def __init__(self):
      print(" Raw plots")
      
  def plot_by_size_features(self,df,labels_to_display,title="Tamanho de amostras por label"):  
      labels_df = df.loc[:,labels_to_display]      
      num_labels=labels_df[labels_df == 1].count().sort_values(ascending=False)
      print (num_labels)
      fig = px.bar(x=labels_to_display, y=num_labels,
                  color=num_labels,
                  height=400,title=title)
      fig.show()
      plot_ly(fig, auto_open=True)

      
 

 
  
 