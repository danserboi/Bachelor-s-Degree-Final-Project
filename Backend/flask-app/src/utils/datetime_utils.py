import time
from datetime import datetime, timedelta, timezone


def utc_now():
    return datetime.now(timezone.utc).replace(microsecond=0)


def local_utc_offset():
    utc_offset = timedelta(seconds=time.localtime().tm_gmtoff)
    return timezone(offset=utc_offset)


def datetime_aware_from_timestamp(timestamp, use_tz=timezone.utc):
    timestamp_naive = datetime.fromtimestamp(timestamp)
    timestamp_aware = timestamp_naive.replace(tzinfo=local_utc_offset())
    return timestamp_aware.astimezone(use_tz)
