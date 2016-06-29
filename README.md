#thesis

How to run:

1. Run TransformInput.java from preprocess package.

2. Run FeatureSelector.java from io package. 
Program arguments: <input_name> <dataset_type> <number_of_result_features>
<input_name>: all input has to be in data/ folder
<dataset_type>: can be 'ads' or 'dorothea'
<number_of_result_features>: number of features to be selected
Do not forget to use Spark configuration, such as master: '-Dspark.master=local[5]' , etc.

3. Run classifier. 
Program arguments: <input_name> <number_of_classes> <proportion_of_training_data>
Example: out/ad_selected_5.data 2 0.7
