from setuptools import setup

with open("README.md", "r") as fh:
    long_description = fh.read()

setup(
    name="pip_dependent_package",
    packages=['pip_dependent_package'],
    license='Apache 2.0',
    version="1.0",
    author="Ankit Tomar",
    author_email="ankit.tomar@strongbox.com",
    description="Test upload package",
    long_description="This is long description",
    long_description_content_type="text/markdown",
    url="https://github.com/anki2189/strongbox-examples",
    keywords=['Hello', 'world', 'pypi', 'dependency'],
    classifiers=[
        'Development Status :: 3 - Alpha',
        'Intended Audience :: Developers',
        "Programming Language :: Python :: 3",
        'License :: Apache License :: Version 2.0',
        "Operating System :: OS Independent",
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
    ],
)
