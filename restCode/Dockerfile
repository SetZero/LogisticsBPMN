FROM tiangolo/uwsgi-nginx-flask:python3.8-alpine
#RUN apk --update add bash nano

WORKDIR /usr/src/app

COPY requirements.txt .

RUN \
 apk add --no-cache postgresql-libs && \
 apk add --no-cache --virtual .build-deps gcc musl-dev postgresql-dev && \
 python3 -m pip install -r requirements.txt --no-cache-dir && \
 apk --purge del .build-deps

COPY . .