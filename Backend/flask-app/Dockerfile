FROM python:3.10.4
WORKDIR /src
ADD requirements.txt .
RUN pip install -r requirements.txt
ADD . .
CMD gunicorn -b 0.0.0.0:5000 run:app


# RUN pip install debugpy

# ENV FLASK_APP=/src/run.py
# ENV FLASK_DEBUG=1
# CMD python -m debugpy --listen 0.0.0.0:5678 ./run.py
