import nbformat
from nbconvert import HTMLExporter
from nbconvert import PythonExporter


def notebook_to_html(notebook_str):
    notebook = nbformat.reads(notebook_str, as_version=4)

    html_exporter = HTMLExporter(template_name='custom_html')
    (html_str, resources) = html_exporter.from_notebook_node(notebook)

    return html_str


def notebook_to_html_print(notebook_str):
    notebook = nbformat.reads(notebook_str, as_version=4)

    html_exporter = HTMLExporter(template_name='custom_html_print')
    (html_str, resources) = html_exporter.from_notebook_node(notebook)

    return html_str


def notebook_to_python(notebook_str):
    notebook = nbformat.reads(notebook_str, as_version=4)

    python_exporter = PythonExporter(template_name='custom_python')
    (python_str, resources) = python_exporter.from_notebook_node(notebook)

    return python_str
