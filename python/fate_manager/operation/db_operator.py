import operator
import peewee
from fate_manager.settings import stat_logger
from arch.common.base_utils import current_timestamp
from fate_manager.db.db_models import DB


class DBOperator:
    @classmethod
    @DB.connection_context()
    def create_entity(cls, entity_model, entity_info):
        instance = entity_model()
        if 'create_time' not in entity_info:
            instance.create_time = current_timestamp()
        if 'update_time' not in entity_info:
            instance.update_time = current_timestamp()
        for k, v in entity_info.items():
            if hasattr(entity_model, k):
                setattr(instance, k, v)
        try:
            rows = instance.save(force_insert=True)
            if rows != 1:
                raise Exception("Create {} failed".format(entity_model))
        except peewee.IntegrityError as e:
            if e.args[0] == 1062:
                stat_logger.warning(e)
            else:
                raise Exception("Create {} failed:\n{}".format(entity_model, e))
        except Exception as e:
            raise Exception("Create {} failed:\n{}".format(entity_model, e))

    @classmethod
    @DB.connection_context()
    def delete_entity(cls, entity_model, **kwargs):
        filters = []
        for k, v in kwargs.items():
            if hasattr(entity_model, k):
                filters.append(operator.attrgetter(k)(entity_model) == v)
        if filters:
            instances = entity_model.select().where(*filters)
            if instances:
                for instance in instances:
                    instance.delete_instance()
                return True
            return False
        return False

    @classmethod
    @DB.connection_context()
    def update_entity(cls, entity_model, entity_info):
        query_filters = []
        primary_keys = entity_model.get_primary_keys_name()
        for p_k in primary_keys:
            if p_k in entity_info:
                query_filters.append(operator.attrgetter(p_k)(entity_model) == entity_info[p_k])
        instances = entity_model.select().where(*query_filters)
        if instances:
            instance = instances[0]
        else:
            # raise Exception("can not found the {}".format(entity_model.__class__.__name__))
            return False
        update_filters = query_filters[:]
        update_info = {}
        update_info.update(entity_info)
        update_info["update_time"] = current_timestamp()
        cls.execute_update(old_obj=instance, model=entity_model,
                           update_info=update_info,
                           update_filters=update_filters)
        return True

    @classmethod
    @DB.connection_context()
    def safe_save(cls, entity_model, entity_info):
        primary_keys = entity_model.get_primary_keys_name()
        filters = []
        for k in primary_keys:
            filters.append(operator.attrgetter(k)(entity_model) == entity_info[k])
        instances = entity_model.select().where(*filters)
        if instances:
            instance = instances[0]
            update_info = {}
            update_info.update(entity_info)
            update_info["update_time"] = current_timestamp()
            for k, v in update_info.items():
                if hasattr(entity_model, k):
                    setattr(instance, k, v)
            instance.save(force_insert=False)
        else:
            instance = entity_model()
            if 'create_time' not in entity_info:
                entity_info["create_time"] = current_timestamp()
            if 'update_time' not in entity_info:
                entity_info["update_time"] = current_timestamp()
            for k, v in entity_info.items():
                if hasattr(entity_model, k):
                    setattr(instance, k, v)
            try:
                rows = instance.save(force_insert=True)
                if rows != 1:
                    raise Exception("Create {} failed".format(entity_model))
            except peewee.IntegrityError as e:
                if e.args[0] == 1062:
                    stat_logger.warning(e)
                else:
                    raise Exception("Create {} failed:\n{}".format(entity_model, e))
            except Exception as e:
                raise Exception("Create {} failed:\n{}".format(entity_model, e))

    @classmethod
    @DB.connection_context()
    def execute_update(cls, old_obj, model, update_info, update_filters):
        update_fields = {}
        for k, v in update_info.items():
            if hasattr(model, k) and k not in model.get_primary_keys_name():
                update_fields[operator.attrgetter(k)(model)] = v
        if update_fields:
            if update_filters:
                stat_logger.info(f'update fields: {update_fields}')
                stat_logger.info(f'update filters: {update_filters}')
                operate = old_obj.update(update_fields).where(*update_filters)
            else:
                operate = old_obj.update(update_fields)
            return operate.execute() > 0
        else:
            return False

    @classmethod
    @DB.connection_context()
    def query_entity(cls, entity_model, reverse=None, order_by=None, **kwargs):
        filters = []
        for k, v in kwargs.items():
            if hasattr(entity_model, k):
                filters.append(operator.attrgetter(k)(entity_model) == v)
        instances = entity_model.select().where(*filters)
        if reverse is not None:
            if not order_by or not hasattr(entity_model, f"{order_by}"):
                order_by = "create_time"
            if reverse is True:
                instances = instances.order_by(getattr(entity_model, f"{order_by}").desc())
            elif reverse is False:
                instances = instances.order_by(getattr(entity_model, f"{order_by}").asc())
        return [instance for instance in instances]


